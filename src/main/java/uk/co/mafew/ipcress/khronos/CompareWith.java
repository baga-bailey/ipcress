

 


package uk.co.mafew.ipcress.khronos;

import java.util.Calendar;

public interface CompareWith
{
	public abstract boolean compareWith(Calendar calendar);

	public String getType();

	public void setType(String type);
}
