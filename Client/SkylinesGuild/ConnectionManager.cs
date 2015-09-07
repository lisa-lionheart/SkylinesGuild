using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using UnityEngine;

namespace SkylinesGuild
{
    class ConnectionManager : MonoBehaviour 
    {
        //Config
        private static String hostname = "localhost";
        private static int port = 8124;

        Socket client;
        public String clientSecret;
        bool authed;
        bool connected;
        String username;

        internal ConnectionManager()
        {
            //Make this random and persisted
            clientSecret = "123456789";
            authed = false;

            StartClient();
        }


        private void StartClient()
        {
            // Connect to a remote device.
            try
            {
                IPHostEntry ipHostInfo = Dns.Resolve(hostname);
                IPAddress ipAddress = ipHostInfo.AddressList[0];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

                client = new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream, ProtocolType.Tcp);

                client.BeginConnect(remoteEP,
                    new AsyncCallback(ConnectCallback), client);
            }
            catch (Exception e)
            {
                Debug.Log(e.ToString());
            }
        }

        private void ConnectCallback(IAsyncResult ar) {
            try
            {
                client.EndConnect(ar);

                Debug.Log("Socket connected");
                connected = true;

                Send("auth", new object[]{clientSecret});
            }
            catch (Exception e)
            {
                Debug.Log(e.ToString());
            }
        }

        private void Send(String method, object[] arguments)

        {
            String data = method + ":";

            foreach(object arg in arguments) {
                data += arg + ",";
            }

            data += "\r\n";

            byte[] byteData = Encoding.ASCII.GetBytes(data);
            client.Send(byteData);
        }

        void Update()
        {
            if (!connected)
                return;

            byte[] data = new byte[1024];
            int bytesRead = client.Receive(data);

            if (bytesRead > 0)
            {
                String message = ASCIIEncoding.UTF8.GetString(data, 0, bytesRead);

                Debug.Log(message);

                String method = message.Substring(0, message.IndexOf(':'));
                String[] args = message.Substring(message.IndexOf(':')+1).Split(',');

                Debug.Log(method);
                Debug.Log(args);


                switch(method){
                    case "auth_sucess":
                        authed = true;
                        username = args[0];
                        break;
                    case "load_game":
                        String downloadUrl = args[0];

                        // Load the game

                        break;

                }

            }

        }

    }
}
