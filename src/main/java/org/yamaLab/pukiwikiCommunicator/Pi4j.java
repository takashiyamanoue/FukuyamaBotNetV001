
package org.yamaLab.pukiwikiCommunicator;

import org.yamaLab.pukiwikiCommunicator.language.InterpreterInterface;
import org.yamaLab.pukiwikiCommunicator.language.Util;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Pi4j implements InterpreterInterface, Runnable{
Thread me;
int counter;
int frequency;
int pie;
	public Pi4j(){
		final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        myButton.setShutdownOptions(true);
        pie = 0;
        
        start();
        System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            //@Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
//                if(pie == 0){
                	if(event.getState().isHigh()){
//                		counter++;
                		pie = 1;
                	}else{
                		pie = 0;
                	}
//                }
            }
        });
       
}
	public String getOutputText() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public boolean isTracing() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public String parseCommand(String x) {
		// TODO 自動生成されたメソッド・スタブ
		String[] rest=new String[1];
		if(Util.parseKeyWord(x,"get-a-0.",rest)){
			               return ""+frequency;
		}
		return "error";
	}

	public InterpreterInterface lookUp(String x) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void start(){
		if(me == null){
			me = new Thread(this,"Pi4j");
			me.start();
		}
	}

	public void stop(){
		me = null;
	}

	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		 System.out.println("start pi4j thread");
		 long lastTime=System.currentTimeMillis();
		 counter=0;
		while(me != null){
			long timeNow=System.currentTimeMillis();
			if(timeNow>=lastTime+10000){
			  frequency = counter;
			  System.out.println("frequency="+frequency);
			  counter = 0;
			    lastTime=timeNow;
			}
			else{
				if(pie==1){
					counter++;
				}
			}
			try{
			Thread.sleep(100);
			}
			catch(InterruptedException e){}
		}
	}
	public void putApplicationTable(String key, InterpreterInterface obj) {
		// TODO Auto-generated method stub
		
	}

}
