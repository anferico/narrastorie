package it.unipi.di.sam.carriage.narrastorie;

import java.util.List;
import java.util.Map;

public class StoryFragment
{
    private String fragmentID;
    private String text;
    private String endType; // Continue, Swipe, Interaction, End
    private Map<String, Object> endOptions;

    public StoryFragment()
    {

    }

    public String getFragmentID()
    {
        return fragmentID;
    }

    public String getText()
    {
        return text;
    }

    public String getEndType()
    {
        return endType;
    }

    public Map<String, Object> getEndOptions()
    {
        return endOptions;
    }

    public void replaceCharactersTokens(List<String> characters)
    {
        for (int i = 0; i < characters.size(); i++)
        {
            text = text.replace("{" + i + "}", characters.get(i));

            if (endOptions != null)
            {
                for (String key : endOptions.keySet())
                {
                    Object value = endOptions.get(key);
                    if (value instanceof String)
                    {
                        String stringValue = (String) value;
                        stringValue = stringValue.replace("{" + i + "}", characters.get(i));
                        endOptions.put(key, stringValue);
                    }
                }
            }
        }
    }

}
