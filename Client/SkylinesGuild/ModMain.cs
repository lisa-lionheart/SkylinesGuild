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

        public void OnRelease()
        {
            Debug.Log("OnRelease");
            go.Disconnect();
        }

    }

}
