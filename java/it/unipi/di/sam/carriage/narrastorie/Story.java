package it.unipi.di.sam.carriage.narrastorie;

import android.content.res.Resources;
import com.google.gson.Gson;
import java.util.List;
import java.util.Random;

public class Story
{

    private Resources resources;
    private List<String> characters;
    private StringBuilder storyStringBuilder;
    private Random random;
    private Gson jsonParser;

    private final int[] macroPlots =
    {
        R.string.S0,
        R.string.S1,
        R.string.S2
    };

    public Story(Resources resources, List<String> characters)
    {
        this.resources = resources;
        this.characters = characters;
        this.storyStringBuilder = new StringBuilder();
        this.random = new Random();
        this.jsonParser = new Gson();
    }

    public StoryFragment firstFragment()
    {
        String jsonString = resources.getString(pickRandomPlot());

        StoryFragment storyFragment = jsonParser.fromJson(jsonString, StoryFragment.class);
        storyFragment.replaceCharactersTokens(characters);

        storyStringBuilder.append(storyFragment.getText());

        return storyFragment;
    }

    public StoryFragment nextFragment(StoryFragment storyFragment)
    {
        String fragmentID = storyFragment.getFragmentID();
        String nextFragmentID;

        String reconnectsTo = (String) storyFragment.getEndOptions().get("reconnectsTo");
        if (reconnectsTo != null)
        {
            nextFragmentID = reconnectsTo;
        }
        else
        {
            Double branches = (Double) storyFragment.getEndOptions().get("branches");
            if (branches != null)
            {
                nextFragmentID = fragmentID + "." + random.nextInt(branches.intValue());
            }
            else
            {
                nextFragmentID = fragmentID + ".0";
            }
        }

        StoryFragment nextStoryFragment = getStoryFragmentByID(nextFragmentID);
        nextStoryFragment.replaceCharactersTokens(characters);

        storyStringBuilder.append(nextStoryFragment.getText());

        return nextStoryFragment;
    }

    public StoryFragment nextFragment(StoryFragment storyFragment, boolean leftSwipe)
    {
        String fragmentID = storyFragment.getFragmentID();
        String nextFragmentID;

        if (leftSwipe)
        {
            nextFragmentID = fragmentID + ".0";
        }
        else
        {
            nextFragmentID = fragmentID + ".1";
        }

        StoryFragment nextStoryFragment = getStoryFragmentByID(nextFragmentID);
        nextStoryFragment.replaceCharactersTokens(characters);

        storyStringBuilder.append(nextStoryFragment.getText());

        return nextStoryFragment;
    }

    private StoryFragment getStoryFragmentByID(String nextFragmentID)
    {
        int nextFragmentResourceID = resources.getIdentifier(
            nextFragmentID,
            "string",
            MainActivity.PACKAGE_NAME
        );

        String nextFragment = resources.getString(nextFragmentResourceID);

        return new Gson().fromJson(
            nextFragment,
            StoryFragment.class
        );
    }

    public int pickRandomPlot()
    {
        return macroPlots[random.nextInt(macroPlots.length)];
    }

    public String getFullStoryText()
    {
        return storyStringBuilder.toString();
    }

}
