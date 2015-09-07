using ColossalFramework;
using ColossalFramework.IO;
using ColossalFramework.Packaging;
using ColossalFramework.Steamworks;
using ColossalFramework.Threading;
using ColossalFramework.UI;
using ICities;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Timers;
using UnityEngine;

namespace SkylinesGuild
{
    public class ModMain : LoadingExtensionBase, IUserMod
    {
        static GuildManager go = null;
        
        public string Name
        {
            get { return "Guild of Skylines Mayors"; }
        }


        public string Description
        {
            get {
                if (go == null) {
                    go = new GuildManager();
                }
                return "Share/collaborate with your fellow mayors"; 
            }

            
        }

    }

    class GuildManager: ClientConnection 
    {

        bool guildGameLoaded = false;
        bool loading = false;
        float lastPing;
        String currentCityId = "";

        LoadingManager loadingManager;
        private bool isSaving;


        String downloadPath = Path.GetTempPath() + "\\Temp.crp";
        String tempSavePath;

        public GuildManager() : base()
        {
            Debug.Log("Guildmanger Init");

            loadingManager = Singleton<LoadingManager>.instance;
            loadingManager.m_levelLoaded += OnLevelLoaded;

            RegisterMessage("load_game", BeginLoadGame);
            
            InitMainMenuButton();

            Timer t = new Timer();
            t.Interval = 10.0f;
            t.Elapsed += OnTimer;
            t.Start();
        }

        

        private void InitMainMenuButton()
        {
            MainMenu mainMenu = MainMenu.m_MainMenu.GetComponent<MainMenu>();
            Debug.Log("Got main menu: " + mainMenu.ToString());

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

        private void RemoveVanillaSaveAndLoadButtons()
        {

        }

       
        private void OnGuildButtonPressed(UIComponent component, UIMouseEventParameter eventParam)
        {
            Steam.ActivateGameOverlayToWebPage("http://127.0.0.1:3000/connect/" + clientSecret);
        }


        public void OnTimer(object source, System.Timers.ElapsedEventArgs e)
        {
            if (guildGameLoaded)
            {

                if (Time.fixedTime - lastPing > 30.0f)
                {

                    String[] args = new String[1];
                    args[0] = currentCityId;

                    //Send("ping", args);

                    Debug.Log("Ping");
                    SaveGame();
                    lastPing = Time.fixedTime;
                }


                if (isSaving)
                    UploadWhenSaveFinished();
            }

        }

        private void SaveGame()
        {
            if (isSaving)
                return;

            SavePanel savePanel = UIView.library.Get<SavePanel>("SavePanel");
            String name = Guid.NewGuid().ToString();
            tempSavePath = DataLocation.saveLocation + name + ".crp";
            isSaving = true;
            savePanel.SaveGame(name);
        }

        private void UploadWhenSaveFinished()
        {
            if (!SavePanel.isSaving)
            {
                isSaving = false;
                Debug.Log("Save finished");
                Debug.Log("Saved as " + tempSavePath);

                byte[] saveData = File.ReadAllBytes(tempSavePath);

                //Now post it to the server
                using(WebClient wc = new WebClient()) {

                    wc.UploadDataCompleted += (object sender, UploadDataCompletedEventArgs e) => {
                    
                        File.Delete(tempSavePath);
                    };
                    wc.UploadDataAsync(new Uri("http://"+hostname+":"+port+"/cities/" + currentCityId), "POST", saveData);
                }
            }   
        }

        private void BeginLoadGame(string[] args)
        {
            if (loading)
            {
                return;
            }

            Debug.Log("Begin load game:" + args[1]);

            loading = true;
            currentCityId = args[0];

            using (WebClient wc = new WebClient())
            {
                wc.DownloadProgressChanged += (object sender, DownloadProgressChangedEventArgs e) =>
                {
                    Debug.Log("Download progress: " + e.BytesReceived);
                };
                wc.DownloadFileCompleted += (object sender, System.ComponentModel.AsyncCompletedEventArgs e) =>
                {
                    LoadSaveGameFromFile(downloadPath);
                };

                wc.DownloadFileAsync(new Uri(args[1]), downloadPath);
            }
        }


        void LoadSaveGameFromFile(String path)
        {
            Package p = new Package(null, path);
            Package.Asset data = p.Find(p.packageMainAsset);

            SaveGameMetaData mmd = data.Instantiate<SaveGameMetaData>();
            Debug.Log(mmd.cityName);
            Debug.Log(mmd.timeStamp.ToString());

            SimulationMetaData simulationMetaData = new SimulationMetaData
            {
                m_CityName = mmd.cityName,
                m_updateMode = SimulationManager.UpdateMode.LoadGame,
                m_environment = ""
            };

            Singleton<LoadingManager>.instance.LoadLevel(mmd.assetRef, "Game", "InGame", simulationMetaData);
        }

        void OnLevelLoaded(SimulationManager.UpdateMode updateMode)
        {
            if (!loading)
            {
                return;
            }
                                
            guildGameLoaded = true;
            loading = false;
            
            //Disable in game autosave
            loadingManager.autoSaveTimer.Stop();
            RemoveVanillaSaveAndLoadButtons();
        }

    }
}
