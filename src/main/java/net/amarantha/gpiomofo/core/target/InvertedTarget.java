package net.amarantha.gpiomofo.core.target;

public class InvertedTarget extends Target {

    private Target target;

    @Override
    public void processTrigger(boolean inputState) {
//        System.out.println("FIRE INVERTED: " + inputState);
//        if ( target!=null ) {
//            System.out.println("Have target");
//            if ( inputState==getTriggerState() ) {
//                target.deactivate(true);
//            } else {
//                target.activate();
//            }
//        }
        super.processTrigger(inputState);
    }

    @Override
    protected void onActivate() {
        if ( target!=null ) {
            target.deactivate(true);
        }
    }

    @Override
    protected void onDeactivate() {
        if ( target!=null ) {
            target.activate();
        }
    }

    public InvertedTarget target(Target target) {
        this.target = target;
        return this;
    }

}
