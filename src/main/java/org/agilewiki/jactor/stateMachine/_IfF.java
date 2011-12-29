package org.agilewiki.jactor.stateMachine;

import org.agilewiki.jactor.ResponseProcessor;

final public class _IfF extends _Go {
    private BooleanFunc condition;

    public _IfF(BooleanFunc condition, String label) {
        super(label);
        this.condition = condition;
    }

    @Override
    public void call(StateMachine stateMachine, ResponseProcessor rp) throws Exception {
        if (condition.get()) super.call(stateMachine, rp);
        else rp.process(null);
    }
}
