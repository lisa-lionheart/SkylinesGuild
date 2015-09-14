using ColossalFramework;
using ColossalFramework.Packaging;
using ColossalFramework.Threading;
using ColossalFramework.UI;
using SharpCompress.Compressor.BZip2;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;

namespace SkylinesGuild
{
    class SavegameLoader
    {

        String downloadPath;
        bool loadingGuildGame = false;
        public string currentCityId;

        GuildManager manager;

        public SavegameLoader(GuildManager m)
        {
            manager = m;
            manager.RegisterMessage("load_city", BeginLoadGame);
        }

       

        public void BeginLoadGame(string[] args)
        {
            Log.Debug("Recived request to load game");

            if (loadingGuildGame)
            {
                Log.Debug("Game already doing a load");
                return;
            }


            Log.Debug("Begin load game:" + args[1]);

            loadingGuildGame = true;
            currentCityId = args[0];

            downloadPath = Path.GetTempPath() + "\\Temp.crp.bzip2";

            using (WebClient wc = new WebClient())
            {

                ProgresView progress = ProgresView.ShowModal("Loading guild game", "Connecting to server");

                progress.OnCanceled += (UIComponent ui) =>
                {
                    wc.CancelAsync();
                };

                wc.DownloadProgressChanged += (object sender, DownloadProgressChangedEventArgs e) =>
                {

                    ThreadHelper.dispatcher.Dispatch(() =>
                    {
                        progress.SetMessage("Recieved " + e.BytesReceived + " of " + e.TotalBytesToReceive + " bytes");
                    });
                };
                wc.DownloadFileCompleted += (object sender, System.ComponentModel.AsyncCompletedEventArgs e) =>
                {
                    if (e.Error != null)
                    {
                        Log.Debug("Error loading game: " + e.Error.ToString());
                    }
                    else
                    {

                        ThreadHelper.dispatcher.Dispatch(() =>
                        {
                            progress.SetMessage("Starting game");
                        });

                        BZip2Stream bzip = new BZip2Stream(new FileStream(downloadPath, FileMode.Open), SharpCompress.Compressor.CompressionMode.Decompress);

                        string decompressFile = Path.GetTempPath() + "\\Temp.crp";
                        FileStream decompressedFile = new FileStream(decompressFile, FileMode.OpenOrCreate, FileAccess.Write);

                        StreamUtil.CopyStream(bzip, decompressedFile);

                        decompressedFile.Close();


                        progress.Close();

                        LoadSaveGameFromFile(decompressFile);
                    }
                };


                wc.DownloadFileAsync(new Uri(args[1]), downloadPath);
            }
        }


        void LoadSaveGameFromFile(String path)
        {
            Package p = new Package(null, path);
            Package.Asset data = p.Find(p.packageMainAsset);

            SaveGameMetaData mmd = data.Instantiate<SaveGameMetaData>();
            Log.Debug(mmd.cityName);
            Log.Debug(mmd.timeStamp.ToString());

            SimulationMetaData simulationMetaData = new SimulationMetaData
            {
                m_CityName = mmd.cityName,
                m_updateMode = SimulationManager.UpdateMode.LoadGame,
                m_environment = ""
            };


            Singleton<LoadingManager>.instance.m_levelLoaded += LoadedGameCallback;
            Singleton<LoadingManager>.instance.LoadLevel(mmd.assetRef, "Game", "InGame", simulationMetaData);
        }

        void LoadedGameCallback(SimulationManager.UpdateMode updateMode)
        {
            if (!loadingGuildGame)
            {
                Log.Debug("Loaded but was not a guild game");
                return;
            }


            loadingGuildGame = false;
            Singleton<LoadingManager>.instance.m_levelLoaded -= LoadedGameCallback;
            manager.OnStartedGuildGame(currentCityId);
        }

    }
}
