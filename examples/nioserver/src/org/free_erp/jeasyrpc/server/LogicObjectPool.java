/*
 * Copyright 2013, TengJianfa , and other individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.free_erp.jeasyrpc.server;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class LogicObjectPool 
{
    private Set<LogicObject> logicObjects;
    private int initialCount = 10;
    
    private static LogicObjectPool logicPool;
    public static LogicObjectPool getLogicObjectPool()
    {
        if (logicPool == null)
        {
            logicPool = new LogicObjectPool();
        }
        return logicPool;
    }
    
    private LogicObjectPool()
    {
        logicObjects = new HashSet<LogicObject>();
        for(int i =0; i < initialCount; i++)
        {
            LogicObject obj = new LogicObject();
            obj.setContainer(this);
            logicObjects.add(obj);
        }
    }
    
    public LogicObject getLogicObject()
    {
        if (logicObjects.size() == 0)
        {
             for(int i =0; i < initialCount; i++)
            {
                LogicObject obj = new LogicObject();
                obj.setContainer(this);
                logicObjects.add(obj);
            }
        }
        LogicObject obj = (LogicObject)logicObjects.toArray()[0];
        logicObjects.remove(obj);
        return obj;
    }
    
    
    
    public void returnObject(LogicObject obj)
    {
        logicObjects.add(obj);
    }
    
}
