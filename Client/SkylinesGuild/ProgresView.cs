using ColossalFramework.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace SkylinesGuild
{

    public delegate void CancelHandler(UIComponent component);

    class ProgresView : UIPanel
    {
        private UILabel title;
        private UILabel message;

        private UIButton abort;

        public event CancelHandler OnCanceled;

        //public override void Start()
        ProgresView()
        {
            backgroundSprite = "MenuPanel";
            width = 600;
            height = 250;
            anchor = UIAnchorStyle.CenterHorizontal | UIAnchorStyle.CenterVertical;
            
            title = AddUIComponent<UILabel>();
            title.autoSize = false;
            title.textAlignment = UIHorizontalAlignment.Center;
            title.size = new Vector2(580, 30);
            title.relativePosition = new Vector2(10, 15);

            message = AddUIComponent<UILabel>();
            message.autoSize = false;
            message.textAlignment = UIHorizontalAlignment.Center;
            message.verticalAlignment = UIVerticalAlignment.Middle;
            message.size = new Vector2(580, 100);
            message.relativePosition = new Vector2(10, 50);

            abort = AddUIComponent<UIButton>();
            abort.text = "Cancel";
            abort.eventClick += abort_eventClick;
            abort.normalBgSprite = "ButtonMenu";
            abort.disabledBgSprite = "ButtonMenuDisabled";
            abort.hoveredBgSprite = "ButtonMenuHovered";
            abort.focusedBgSprite = "ButtonMenu";
            abort.pressedBgSprite = "ButtonMenuPressed";
            abort.size = new Vector2(200, 50);
            abort.relativePosition = new Vector2(200, 190);
        }

        public void SetTitle(String title)
        {
            this.title.text = title;
        }

        public void SetMessage(String message)
        {
            this.message.text = message;
        }

        void abort_eventClick(UIComponent component, UIMouseEventParameter eventParam)
        {
            if (OnCanceled != null)
            {
                OnCanceled(this);
            }
            Close();
        }

        public static ProgresView ShowModal(String title, String message)
        {
            ProgresView view = (ProgresView)UIView.GetAView().AddUIComponent(typeof(ProgresView));
            UIView.PushModal(view);

            view.SetTitle(title);
            view.SetMessage(message);
            return view;
        }

        public void Close()
        {
            UIView.PopModal();
            GameObject.Destroy(gameObject);
        }
        

    }
}
