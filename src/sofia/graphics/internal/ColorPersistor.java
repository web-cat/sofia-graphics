package sofia.graphics.internal;

import java.util.Map;

import sofia.graphics.Color;

public class ColorPersistor
{
	// ----------------------------------------------------------
	public static void represent(Object obj, Map<String, Object> rep)
	{
		Color color = (Color) obj;
		rep.put("value", color.toRawColor());
	}


	// ----------------------------------------------------------
	public static Color construct(Map<String, Object> rep)
	{
		int value = (Integer) rep.get("value");
		return Color.fromRawColor(value);
	}
}
