using ColossalFramework.IO;
using ColossalFramework.Threading;
using System;
using System.Collections.Generic;
using System.IO;
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

        Socket client;
        bool authed;
        bool connected;
        bool running = true;
        String username;

        Thread socketThread;
        
        Dictionary<String, Action<String[]>> handlers;
        Queue<byte[]> sendQueue = new Queue<byte[]>();

        public ClientConnection()
        {
            handlers = new Dictionary<String, Action<String[]>>();

           
                        
            authed = false;

            socketThread = new Thread(this.StartClient);
            socketThread.Start();
        }

        public void RegisterMessage(String name, Action<String[]> handler) {
            handlers.Add(name, handler);
        }

        public void Disconnect() {
            running = false;
            
        }

        private void StartClient()
        {

            while (running)
            {
                // Connect to a remote device.
                try
                {
                    IPHostEntry ipHostInfo = Dns.Resolve(Config.hostname);
                    IPAddress ipAddress = ipHostInfo.AddressList[0];
                    IPEndPoint remoteEP = new IPEndPoint(ipAddress, Config.port);

                    client = new Socket(AddressFamily.InterNetwork,
                        SocketType.Stream, ProtocolType.Tcp);
                    
                    client.Connect(remoteEP);
                    client.ReceiveTimeout = 100;

                    Log.Debug("Socket connected");
                    connected = true;

                    Send("auth", new String[] { Config.clientSecret() });

                    while (running)
                    {

                        try
                        {
                            byte[] data = new byte[1024];
                            int bytesRead = client.Receive(data, 1024, 0);


                            Log.Debug("Got " + bytesRead + " bytes");
                            if (bytesRead > 0)
                            {
                                String message = ASCIIEncoding.UTF8.GetString(data, 0, bytesRead);

                                Log.Debug(message);

                                String method = message.Substring(0, message.IndexOf(':'));
                                String[] args = message.Substring(message.IndexOf(':') + 1).Split(',');

                                Log.Debug(method);
                                Log.Debug(args);

                                ThreadHelper.dispatcher.Dispatch(() => { RecivedData(method, args); });
                            }
                        }
                        catch (SocketException e)
                        {
                            
                        }

                        lock (sendQueue) 
                        {
                            while (sendQueue.Count > 0)
                            {
                                client.Send(sendQueue.Dequeue());
                            }
                        }

                    }

                }
                catch (Exception e)
                {
                    Log.Debug(e.ToString());
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
            lock (sendQueue)
            {
                sendQueue.Enqueue(byteData);
            }
            
        }

        void RecivedData(String method, String[] args) {

            Log.Debug("Dispatch message: " + method + "args: " + args.Length);
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
