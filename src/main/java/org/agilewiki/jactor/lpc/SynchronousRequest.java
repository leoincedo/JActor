/*
 * Copyright 2012 Bill La Forge
 *
 * This file is part of AgileWiki and is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (LGPL) as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * or navigate to the following url http://www.gnu.org/licenses/lgpl-2.1.txt
 *
 * Note however that only Scala, Java and JavaScript files are being covered by LGPL.
 * All other files are covered by the Common Public License (CPL).
 * A copy of this license is also included and can be
 * found as well at http://www.opensource.org/licenses/cpl1.0.txt
 */
package org.agilewiki.jactor.lpc;

import org.agilewiki.jactor.Actor;
import org.agilewiki.jactor.Mailbox;

/**
 * A request that can be passed to a JBActor for synchronous processing,
 * but only when sender and receiver use the same mailbox.
 */
abstract public class SynchronousRequest<RESPONSE_TYPE, TARGET_TYPE extends TargetActor>
        extends CallableRequest<RESPONSE_TYPE, TARGET_TYPE> {

    /**
     * Send a synchronous request.
     *
     * @param srcActor    The sender of the request.
     * @param targetActor The target actor.
     * @return The response.
     * @throws Exception Any uncaught exceptions raised while processing the request.
     */
    final public RESPONSE_TYPE call(Actor srcActor, Actor targetActor)
            throws Exception {
        if (isTargetType(targetActor))
            return call(srcActor, (TARGET_TYPE) targetActor);
        Actor parent = targetActor.getParent();
        if (parent != null)
            return call(srcActor, parent);
        throw new UnsupportedOperationException();
    }

    /**
     * Send a synchronous request.
     *
     * @param srcActor    The sender of the request.
     * @param targetActor The target actor.
     * @return The response.
     * @throws Exception Any uncaught exceptions raised while processing the request.
     */
    final public RESPONSE_TYPE call(Actor srcActor, TARGET_TYPE targetActor)
            throws Exception {
        Mailbox sourceMailbox = srcActor.getMailbox();
        if (sourceMailbox != targetActor.getMailbox())
            throw new UnsupportedOperationException("Mailboxes are not the same.");
        return call(targetActor);
    }

    /**
     * Send a synchronous request.
     *
     * @param targetActor The target actor.
     * @return The response.
     * @throws Exception Any uncaught exceptions raised while processing the request.
     */
    final protected RESPONSE_TYPE call(TARGET_TYPE targetActor)
            throws Exception {
        return _call(targetActor);
    }
}
