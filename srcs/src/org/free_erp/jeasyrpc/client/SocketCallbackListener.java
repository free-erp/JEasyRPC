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

package org.free_erp.jeasyrpc.client;

import org.free_erp.jeasyrpc.CallInfo;
import org.free_erp.jeasyrpc.ReturnInfo;
import org.free_erp.jeasyrpc.TransferInfo;

/**
 *
 * @author afa
 */
public interface SocketCallbackListener
{
    public void methodReturned(ReturnInfo info);

    public void dataTransfered(TransferInfo info);

    public void methodCall(CallInfo info);
}
