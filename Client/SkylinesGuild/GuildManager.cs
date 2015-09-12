using ColossalFramework;
using ColossalFramework.IO;
using ColossalFramework.Packaging;
using ColossalFramework.Steamworks;
using ColossalFramework.UI;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Timers;
using UnityEngine;
using System.Runtime.Serialization;
using ColossalFramework.Threading;

namespace SkylinesGuild
{
    class Log
    {
        public static void Debug(object msg) {
#if DEBUG
            UnityEngine.Debug.Log(msg);
#endif
        }

        internal static void Trace()
        {
            UnityEngine.Debug.Log(Environment.StackTrace.Split(Environment.NewLine[0])[1]);
        }
    }
    

    class GuildManager : ClientConnection
    {

        public static float saveInterval = 30.0f;

        bool guildGameLoaded = false;
        bool loadingGuildGame = false;
        float lastPing, lastSave;
        String currentCityId = "";

        LoadingManager loadingManager;

        bool modActive;

        String downloadPath = Path.GetTempPath() + "\\Temp.crp";
        private SaveGamePublisher publisher;


        InGameUI inGameUi;


        public GuildManager()
            : base()
        {
            Log.Debug("Guildmanger Init");
            
            RegisterMessage("load_city", BeginLoadGame);


            Timer t = new Timer();
            t.Interval = 10.0f;
            t.Elapsed += OnTimer;
            t.Start();
        }


        public bool Enabled
        {
            set
            {
                if (!modActive && value)
                {
                    InitMainMenuButton();
                }

                if (modActive && !value)
                {
                    ResetMainMenuButton();
                }

                modActive = value;

            }
            get
            {
               return  modActive;
            }
        }

        private void ResetMainMenuButton()
        {
            MainMenu mainMenu = MainMenu.m_MainMenu.GetComponent<MainMenu>();
            UIButton guildButton = mainMenu.Find<UIButton>("guildButton");
            mainMenu.component.RemoveUIComponent(guildButton);
        }


        private void InitMainMenuButton()
        {
            MainMenu mainMenu = MainMenu.m_MainMenu.GetComponent<MainMenu>();
            Log.Debug("Got main menu: " + mainMenu.ToString());

            GameObject asGameObject = UITemplateManager.GetAsGameObject("MainMenuButtonTemplate");
            asGameObject.name = "guildbutton";
            UIComponent uIComponent = mainMenu.component.AttachUIComponent(asGameObject);
            UIButton uIButton = uIComponent as UIButton;
            if (uIButton != null)
            {
                uIButton.text = "Skylines Guild";
                uIButton.stringUserData = "guild";
            }

            mainMenu.component.FitChildren();
            uIButton.eventMouseUp += OnGuildButtonPressed;
        }



        private void OnGuildButtonPressed(UIComponent component, UIMouseEventParameter eventParam)
        {
            Steam.ActivateGameOverlayToWebPage("http://127.0.0.1:8080/connect/" + Config.clientSecret());
        }


        public void OnTimer(object source, System.Timers.ElapsedEventArgs e)
        {

            if (Time.fixedTime - lastPing > 30.0f)
            {

                String[] args = new String[1];
                args[0] = currentCityId;

                Send("ping", args);
                lastPing = Time.fixedTime;
            }

            if (guildGameLoaded)
            {
                //if (Time.fixedTime - lastSave > saveInterval)
                //{
                //    lastSave = Time.fixedTime;
                //    publisher.SaveGame();
                //}

            }

        }


        private void BeginLoadGame(string[] args)
        {

            Log.Debug("Recived request to load game");

            if (loadingGuildGame)
            {
                Log.Debug("Game already loaded");
                return;
            }

            Log.Debug("Begin load game:" + args[1]);

            loadingGuildGame = true;
            currentCityId = args[0];

            using (WebClient wc = new WebClient())
            {

                ConfirmPanel progressPanel;
                progressPanel = UIView.library.ShowModal<ConfirmPanel>("ConfirmPanel");
                       
                progressPanel.Find("Yes").enabled = false;
                progressPanel.Find("No").enabled = false;
                

                wc.DownloadProgressChanged += (object sender, DownloadProgressChangedEventArgs e) =>
                {

                    ThreadHelper.dispatcher.Dispatch(() =>
                    {
                        progressPanel.SetMessage("Downloading game...", "Recieved " + e.BytesReceived + " of " + e.TotalBytesToReceive + " bytes");
                    });
                };
                wc.DownloadFileCompleted += (object sender, System.ComponentModel.AsyncCompletedEventArgs e) =>
                {
                    if (e.Error != null)
                    {
                        Log.Debug("Error loading game: " + e.Error.ToString());
                    }
                    else {

                        progressPanel.Find("Yes").enabled = true;
                        progressPanel.Find("No").enabled = true;
                        progressPanel.OnClosed();

                        LoadSaveGameFromFile(downloadPath);
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

            Singleton<LoadingManager>.instance.LoadLevel(mmd.assetRef, "Game", "InGame", simulationMetaData);
        }

        public void OnLevelLoaded()
        {
            if (!loadingGuildGame)
            {
                Log.Debug("Loaded but was not a guild game");
                return;
            }

            Log.Debug("Loaded guild game");

            guildGameLoaded = true;
            loadingGuildGame = false;

            inGameUi = new GameObject().AddComponent <InGameUI>();
            inGameUi.Init(currentCityId);
            

            //Disable in game autosave
            loadingManager.autoSaveTimer.Stop();
            
        }

    }
}
