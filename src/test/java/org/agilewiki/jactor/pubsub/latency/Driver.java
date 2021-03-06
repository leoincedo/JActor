package org.agilewiki.jactor.pubsub.latency;

import org.agilewiki.jactor.Mailbox;
import org.agilewiki.jactor.RP;
import org.agilewiki.jactor.lpc.JLPCActor;
import org.agilewiki.jactor.pubsub.publisher.JAPublisher;
import org.agilewiki.jactor.pubsub.publisher.Publish;
import org.agilewiki.jactor.pubsub.publisher.Subscribe;
import org.agilewiki.jactor.stateMachine.ExtendedResponseProcessor;

/**
 * Test code.
 */
public class Driver extends JLPCActor implements Src {
    public int r;
    public int s;
    public JAPublisher pub;
    private int count;
    private Publish publish = new Publish(Ping.req);

    public Driver(final Mailbox mailbox) {
        super(mailbox);
    }

    public void sender(final RP done, ExtendedResponseProcessor<Integer> erp) throws Exception {
        while (true) {
            if (count == r) {
                done.processResponse(null);
                return;
            }
            count += 1;
            erp.sync = false;
            erp.async = false;
            publish.send(this, pub, erp);
            if (!erp.sync) {
                erp.async = true;
                return;
            }
        }
    }

    @Override
    protected void processRequest(Object request, final RP rp) throws Exception {
        Class reqcls = request.getClass();

        if (reqcls == Go.class) {
            count = 0;
            int i = 0;
            while (i < s) {
                Sub sub = new Sub(getMailbox());
                sub.setActorName("" + i);
                sub.src = this;
                Subscribe subscribe = new Subscribe(sub);
                subscribe.sendEvent(this, pub);
                i += 1;
            }
            ExtendedResponseProcessor<Integer> erp = new ExtendedResponseProcessor<Integer>() {
                @Override
                public void processResponse(Integer response) throws Exception {
                    if (!async)
                        sync = true;
                    else
                        sender(rp, this);
                }
            };
            sender(rp, erp);
            return;
        }

        throw new UnsupportedOperationException(reqcls.getName());
    }
}
