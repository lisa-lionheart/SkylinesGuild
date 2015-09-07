using ColossalFramework;
using ColossalFramework.Steamworks;
using ColossalFramework.UI;
using ICities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UnityEngine;

namespace SkylinesGuild
{
    public class ModMain : LoadingExtensionBase, IUserMod
    {
        
        public string Name
        {
            get { return "Guild of Skylines Mayors"; }
        }


        public string Description
        {
            get { Init(); return "Share/collaborate with your fellow mayors"; }

            
        }

        void Init()
        {
            Singleton<ConnectionManager>.Ensure();
            InitMainMenuButton();
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

        private void OnGuildButtonPressed(UIComponent component, UIMouseEventParameter eventParam)
        {

            String secret = Singleton<ConnectionManager>.instance.clientSecret;
            Steam.ActivateGameOverlayToWebPage("http://127.0.0.1:3000/connect/" + secret);

        }


    }
}
