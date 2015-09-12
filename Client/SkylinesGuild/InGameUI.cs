using ColossalFramework.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace SkylinesGuild 
{
    class InGameUI : MonoBehaviour
    {
        UIButton saveNowButton;
        SaveGamePublisher publisher;
        
        public void Init(String currentCityId)
        {
            Log.Trace();


            publisher = new SaveGamePublisher(currentCityId);

            PauseMenu pauseMenu = UIView.library.Get<PauseMenu>("PauseMenu");

            UIButton loadGameButton = pauseMenu.Find<UIButton>("LoadGame");
            loadGameButton.enabled = false;

            UIButton saveGameButton = pauseMenu.Find<UIButton>("SaveGame");

            GameObject asGameObject = UITemplateManager.GetAsGameObject("PauseMenuButtonTemplate");
            asGameObject.name = "uploadNow";


            saveNowButton = loadGameButton.parent.AttachUIComponent(asGameObject) as UIButton;
            saveNowButton.text = "Upload Now";
            saveNowButton.stringUserData = "guild";

            saveNowButton.position = saveGameButton.position;
            saveNowButton.size = saveGameButton.position;


            saveGameButton.enabled = false;
            
            //loadGameButton.parent.FitChildren();
            saveNowButton.eventMouseUp += OnUploadPressed;

        }


        private void OnUploadPressed(UIComponent component, UIMouseEventParameter eventParam)
        {
            component.Disable();
            publisher.SaveGame();
        }
    }


}
