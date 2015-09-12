using ColossalFramework;
using ColossalFramework.UI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SkylinesGuild 
{
    class AsyncCallbackAction : AsyncAction
    {
        Action<Action<Exception>> m_asyncAction;

        public AsyncCallbackAction(String name, Action<Action<Exception>> asyncAction) : base(null,name)
        {
            m_asyncAction = asyncAction;
        }

        public override void Execute()
        {
            try
            {
                this.m_Progress = 0f;
                
                this.m_asyncAction((Exception e) =>{

                    if(e != null) {          
                        this.m_Progress = 100f;
                        UIView.ForwardException(e);
                    }else {
                        this.m_Progress = 1f;
                    }
                    
                });
                
            }
            catch (Exception ex)
            {
                this.m_Progress = 100f;
                UIView.ForwardException(ex);
                CODebugBase<LogChannel>.Error(LogChannel.Core, "Execution error: " + ex.Message + "\n" + ex.StackTrace);
            }
        }
    }
}
