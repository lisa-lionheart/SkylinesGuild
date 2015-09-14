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
using SharpCompress.Compressor.BZip2;

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

        float lastPing;

        SavegameLoader loader;

        bool modActive;

        InGameUI inGameUi;


        public GuildManager()
            : base()
        {
            Log.Debug("Guildmanger Init");
            

            loader = new SavegameLoader(this);


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
                args[0] = loader.currentCityId;

                Send("ping", args);
                lastPing = Time.fixedTime;
            }

        }


        public void OnStartedGuildGame(String cityId)
        {
            
            Log.Debug("Loaded guild game");

            inGameUi = new GameObject().AddComponent<InGameUI>();
            inGameUi.Init(cityId);

            //Disable in game autosave
            Singleton<LoadingManager>.instance.autoSaveTimer.Stop();
        }



       

    }
}
