package uk.co.mafew.ipcress.khronos;

import java.util.*;

import org.w3c.dom.Element;

import uk.co.mafew.ipcress.narcissus.ReflectedObject;
import uk.co.mafew.ipcress.apollo.logging.*;


public class Alarm
{
	Timer timer;
	Date alarmDate;
	Calendar cal;
	Iterator it;
	Object ob;
	Logger logger;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	public Alarm(Element targetClass)
	{
		logger = new Logger(this.getClass().getName());
		alarmDate = new Date();
		timer = new Timer();
		timer.schedule(new SoundAlarm(targetClass), alarmDate);
	}

	public Alarm()
	{
		logger = new Logger(this.getClass().getName());
	}
	
	public Date initialise(Iterator it, Element targetClass)
	{
		this.it = it;
		alarmDate = new Date(this.it.next().getTimeInMillis());
		logger.log.debug("Alarm initiated using date " + alarmDate.toString());
		
		timer = new Timer();
		timer.schedule(new SoundAlarm(targetClass), alarmDate);
		return alarmDate;
	}

//	public void alarmTest()
//	{
//
//	}

	class SoundAlarm extends TimerTask
	{
		private Element targetClass;
		private ReflectedObject ro;

		public SoundAlarm(Element targetClass)
		{
			this.targetClass = targetClass;
			try
			{
				ro = new ReflectedObject(targetClass,"");
			} 
			catch(NoSuchMethodException ne)
			{
				logger.log.error("THE METHOD WAS NOT FOUND");
			}catch (Exception e)
			{
				logger.log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
		public SoundAlarm(ReflectedObject ro)
		{
			this.ro = ro;
		}

		public void run()
		{
			try
			{
				ro.invoke();
			}
			catch (Exception e)
			{
				logger.log.error(e.getMessage());
			}
			alarmDate = new Date(it.next().getTimeInMillis());
			timer = new Timer();
			
			if(!ro.getKeepAlive())
			{
				timer.schedule(new SoundAlarm(targetClass), alarmDate);
			}
			else
			{
				try
				{
					timer.schedule(new SoundAlarm(ro), alarmDate);
				} catch (Exception e)
				{
					logger.log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

}
