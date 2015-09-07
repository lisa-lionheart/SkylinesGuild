using ColossalFramework.Threading;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

namespace SkylinesGuild
{
    class ClientConnection 
    {
        //Config
        public static String hostname = "localhost";
        public static int port = 8124;

        Socket client;
        public String clientSecret;
        bool authed;
        bool connected;
        String username;

        Thread socketThread;
        
        Dictionary<String, Action<String[]>> handlers;

        public ClientConnection()
        {
            handlers = new Dictionary<String, Action<String[]>>();

            clientSecret = Guid.NewGuid().ToString();
            authed = false;

            socketThread = new Thread(this.StartClient);
            socketThread.Start();
        }

        public void RegisterMessage(String name, Action<String[]> handler) {
            handlers.Add(name, handler);
        }

        private void StartClient()
        {

            while (true)
            {
                // Connect to a remote device.
                try
                {
                    IPHostEntry ipHostInfo = Dns.Resolve(hostname);
                    IPAddress ipAddress = ipHostInfo.AddressList[0];
                    IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

                    client = new Socket(AddressFamily.InterNetwork,
                        SocketType.Stream, ProtocolType.Tcp);
                    
                    client.Connect(remoteEP);

                    Debug.Log("Socket connected");
                    connected = true;

                    Send("auth", new String[] { clientSecret });

                    while (true)
                    {
                        byte[] data = new byte[1024];
                        int bytesRead = client.Receive(data, 1024, 0);

                        Debug.Log("Got " + bytesRead + " bytes");
                        if (bytesRead > 0)
                        {
                            String message = ASCIIEncoding.UTF8.GetString(data, 0, bytesRead);

                            Debug.Log(message);

                            String method = message.Substring(0, message.IndexOf(':'));
                            String[] args = message.Substring(message.IndexOf(':')+1).Split(',');

                            Debug.Log(method);
                            Debug.Log(args);

                            ThreadHelper.dispatcher.Dispatch(()=>{ RecivedData(method,args); });
                        }

                    }

                }
                catch (Exception e)
                {
                    Debug.Log(e.ToString());
                }

            }
        }

      
        protected void Send(String method, String[] arguments)

        {
            String data = method + ":";

            foreach(object arg in arguments) {
                data += arg + ",";
            }

            data += Time.fixedTime;
            data += "\r\n";

            byte[] byteData = Encoding.ASCII.GetBytes(data);
            client.Send(byteData);
        }

        void RecivedData(String method, String[] args) {

            Debug.Log("Dispatch message: " + method + "args: " + args.Length);
            switch(method){
                case "auth_success":
                    authed = true;
                    username = args[0];
                    break;
                default:

                    Action<String[]> handler;
                    if(handlers.TryGetValue(method,out handler)){
                        handler(args);
 
                    }

                    break;

            }

            
        }

        

    }
}
