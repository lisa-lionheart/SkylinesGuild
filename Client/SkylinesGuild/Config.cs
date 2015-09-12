using ColossalFramework.IO;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace SkylinesGuild
{
    class Config
    {

        //Config
        public static String hostname = "localhost";
        public static int port = 8132;
        public static int webPort = 8080;

        private static String _clientSecret;

        public static String clientSecret() {

            String path = Path.Combine(DataLocation.localApplicationData, "guild-clientsecret.txt");
            if (File.Exists(path))
            {
                _clientSecret = File.ReadAllText(path);
            }
            else
            {
                _clientSecret = Guid.NewGuid().ToString();
                File.WriteAllText(path, _clientSecret);
            }

            return _clientSecret;
        }

    }
}
