using ColossalFramework;
using ColossalFramework.IO;
using ColossalFramework.Packaging;
using ColossalFramework.Plugins;
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
    public class ModMain : IUserMod, ILoadingExtension
    {
        static GuildManager go = null;


        public string Name
        {
            get {
                
                
                return "Guild of Skylines Mayors"; 
            }
        }


        public string Description
        {
            get {
                if (go == null)
                {
                    go = new GuildManager();
                }

                PluginManager manager = Singleton<PluginManager>.instance;

                foreach (ColossalFramework.Plugins.PluginManager.PluginInfo plugin in manager.GetPluginsInfo())
                {
                    Log.Debug("Plugin: " + plugin.name + ", isEnabled:" + plugin.isEnabled);
                    
                    if (plugin.userModInstance == this)
                    {
                        go.Enabled = plugin.isEnabled;
                    }
                }
                return "Share/collaborate with your fellow mayors"; 
            }

        }

        public void OnEnabled()
        {
            if (go != null)
            {
                go.Enabled = true;
            }
        }

        public void OnDisabled()
        {
            if (go != null)
            {
                go.Enabled = false;
            }
        }
        
        public void OnCreated(ILoading loading)
        {
            
        }

        public void OnLevelLoaded(LoadMode mode)
        {
            Log.Trace();
            if (mode == LoadMode.LoadGame)
            {
                go.OnLevelLoaded();
                
            }
        }

        public void OnLevelUnloading()
        {
            Log.Trace();
        }

        public void OnReleased()
        {

        }
    }

}
