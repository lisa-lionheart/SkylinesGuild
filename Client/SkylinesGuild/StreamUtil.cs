using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace SkylinesGuild
{
    class StreamUtil
    {
        public static void CopyStream(Stream input, Stream output)
        {
            byte[] buffer = new byte[0x1000];
            int read;
            while ((read = input.Read(buffer, 0, buffer.Length)) > 0)
                output.Write(buffer, 0, read);
        }
    }
}
