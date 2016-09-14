package za.ac.cut.puo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hodielisrael on 2016/09/14.
 */

public class PUOHelper {

    //Word List BoilerPlate
    public static List<Word> populateWordList() {
        List<Word> words = new ArrayList<>();

        //English Words
        words.add(new Word("eutaxy", "English", "good order or management", "noun"));
        words.add(new Word("dabster", "English", "Slang. an expert", "noun"));
        words.add(new Word("pulverulent", "English", "covered with dust or powder.", "adjective"));
        words.add(new Word("vilipend", "English", "to vilify; depreciate", "verb"));

        //Afrikaans Words
        words.add(new Word("vark", "Afrikaans", "an omnivorous domesticated hoofed mammal", "noun"));
        words.add(new Word("evalueer", "Afrikaans", "evaluate or estimate the nature, ability, or quality of", "verb"));
        words.add(new Word("piek", "Afrikaans", "a thin, pointed piece of metal, wood, or another rigid material", "noun"));
        words.add(new Word("golf", "Afrikaans", "a gesture or signal made by moving one's hand to and fro", "noun"));

        //Zulu Words
        words.add(new Word("ukuzibulala", "Zulu", "the action of killing oneself intentionally", "noun"));
        words.add(new Word("bazizwa", "Zulu", "experience (an emotion or sensation)", "verb"));
        words.add(new Word("kuzwakale", "Zulu", "in good condition; not damaged, injured, or diseased", "adjective"));
        words.add(new Word("ingadi", "Zulu", "a piece of ground, often near a house, used for growing flowers, fruit, or vegetablesy", "noun"));

        //Xhosa Words
        words.add(new Word("inja", "Xhosa", "a domesticated carnivorous mammal that typically has a long snout", "noun"));
        words.add(new Word("Dudula", "Xhosa", "an act of exerting force on someone or something", "verb"));
        words.add(new Word("umzabalazo", "Xhosa", "a forceful or violent effort to get free of restraint or resist attack", "noun"));
        words.add(new Word("thetha", "Xhosa", "conversation; discussion", "noun"));
       
        return words;
    }

}
