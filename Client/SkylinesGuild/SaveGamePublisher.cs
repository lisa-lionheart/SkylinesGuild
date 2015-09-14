using ColossalFramework;
using ColossalFramework.IO;
using ColossalFramework.Packaging;
using ColossalFramework.UI;
using MiscUtil.Conversion;
using MiscUtil.IO;
using SharpCompress.Compressor;
using SharpCompress.Compressor.BZip2;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Timers;
using UnityEngine;

namespace SkylinesGuild
{
    class SaveGamePublisher 
    {

        static int formatVersion = 2;
        static int maxSaveWait = 20;

        MemoryStream currentSaveData;
        EndianBinaryWriter saveWriter;

        int waitTimeoutCounter = 0;

        String tempSavePath;
        String cityId;


        public SaveGamePublisher(String cityId) 
        {
            this.cityId = cityId;
        }

        public void SaveGame()
        {
            if (isSaving()) {
                Log.Debug("Already doing a save: " + tempSavePath);
            }


            String name = Guid.NewGuid().ToString();

            Log.Debug("Request save: " + name);

            currentSaveData = new MemoryStream();
            BZip2Stream bzip = new BZip2Stream(currentSaveData, CompressionMode.Compress);
            
            saveWriter = new EndianBinaryWriter(new BigEndianBitConverter(),bzip, Encoding.UTF8);

            saveWriter.Write((byte)formatVersion);

            saveWriter.Write(Singleton<SimulationManager>.instance.m_metaData.m_CityName);
            saveWriter.Write(Singleton<EconomyManager>.instance.LastCashAmount);
            saveWriter.Write(Singleton<EconomyManager>.instance.LastCashDelta);

            int population = (int)Singleton<DistrictManager>.instance.m_districts.m_buffer[0].m_populationData.m_finalCount;
            saveWriter.Write(population);

            var timeSpan = (Singleton<SimulationManager>.instance.m_currentGameTime - new DateTime(1970, 1, 1, 0, 0, 0));
            saveWriter.Write(timeSpan.TotalSeconds);


            SavePanel savePanel = UIView.library.Get<SavePanel>("SavePanel");
            tempSavePath = Path.Combine(DataLocation.saveLocation, name + ".crp");
           
            savePanel.SaveGame(name);
            WaitForSave();
        }

        bool isSaving()
        {
            return saveWriter != null;
        }

        void WaitForSave() {
            
            Log.Debug("Waiting for save to finish");
            Timer t = new Timer(100.0f);
            t.Elapsed += (object sender, ElapsedEventArgs e) =>
            {
                if (!File.Exists(tempSavePath))
                {
                    Log.Debug("Save file does not exist yet :(");
                    waitTimeoutCounter++;
                    if (waitTimeoutCounter > maxSaveWait)
                    {
                        Log.Debug("Wait period exceeded, aborting");
                        t.Stop();
                        Reset();
                    }
                }
                else
                {

                    Log.Debug("Save finished");
                    t.Stop();

                    AsyncCallbackAction task = new AsyncCallbackAction("Uploading to server...", PublishGame);
                    LoadSaveStatus.activeTask = task;
                    task.Execute();

                }
            };
            t.Start();
        }

        void PublishGame(Action<Exception> done)
        {   

            Log.Debug("Saved as " + tempSavePath);

            //Extract Meta data
            Package p = new Package(null, tempSavePath);
            Package.Asset data = p.Find(p.packageMainAsset);

            SaveGameMetaData mmd = data.Instantiate<SaveGameMetaData>();
            Log.Debug(mmd.cityName);
            Log.Debug(mmd.timeStamp.ToString());

            Texture2D thumbnail = (Texture2D)mmd.imageRef.Instantiate<Texture>();


            byte[] thumbData = thumbnail.EncodeToPNG();
            Log.Debug("Thumbnail Length: " + thumbData.Length);
            saveWriter.Write(thumbData.Length);
            saveWriter.Write(thumbData);

            byte[] saveData = File.ReadAllBytes(tempSavePath);
            Log.Debug("Save file length: " + saveData.Length);
            saveWriter.Write(saveData.Length);
            saveWriter.Write(saveData);

            saveWriter.Close();

            Log.Debug("Upload length: "+ saveData.Length);
#if DEBUG
            //Save for debugging later
            String debugPath = Path.Combine(DataLocation.localApplicationData, "upload.bin");
            File.WriteAllBytes(debugPath, currentSaveData.ToArray());
#endif
            //Now post it to the server
            Uri uri = new Uri("http://" + Config.hostname + ":" + Config.webPort + "/city/" + cityId);

            using (WebClient wc = new WebClient())
            {
                wc.Headers.Set("X-ClientSecret", Config.clientSecret());
                wc.Headers.Set("Content-Type", "application/octect-stream");

                wc.UploadDataCompleted += (object sender, UploadDataCompletedEventArgs e) =>
                {
                    Log.Debug("Upload completed");
                    Reset();
                    done(null);
                };
                wc.UploadDataAsync(uri, "POST", currentSaveData.ToArray());
            }
            
        }

        void Reset()
        {
            waitTimeoutCounter = 0;
            currentSaveData = null;
            saveWriter = null;

            if (File.Exists(tempSavePath))
            {
                File.Delete(tempSavePath);
            }
        }
    }
}
