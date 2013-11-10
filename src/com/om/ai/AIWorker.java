package com.om.ai;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 11/9/13
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AIWorker {

    static String allWords = "HIGHER,div,typebutton,class,classbtn,h3stocks,near,face,nov,est,economic,releases,FINANCIAL,MARKETS,DATA,recovery,SYSTEMS,JOHN WILLIAMS,NASDAQ,Island,analysts,board,factset,general,net,tech,piper,jaffray,online,composite,business,EXECUTIVE,leading,shares,semiconductor,trading,travel,gain,trade,hot,Million,check,value,good,Location,management,weight,space,gaga,Growth,asia,sports,place,technology,new,long,town,big,talk,check,market,to, they, but, said, second, for, #tweetreview, no, being, by, its, has, been, of, are, would, his, tweeting, she, only, on, ok, her, slow, quot, had, nbsp, these, live, be, final, Details, rsquo, com, amp, http, href, statuses, search, ago, link, hours, comments, a, about, above, across, after, afterwards, again, against, all, almost, alone, along, already, also, although, always, am, among, amongst, amoungst, amount, an, and, another, any, anyhow, anyone, anything, anyway, anywhere, around, as, at, back, became, because, become, becomes, becoming, before, beforehand, behind, below, beside, besides, between, beyond, bill, both, bottom, call, can, cannot, cant, co, con, could, couldnt, cry, de, describe, detail, do, done, down, due, during, each, eg, eight, either, eleven, else, elsewhere, empty, enough, etc, even, ever, every, everyone, everything, everywhere, except, few, fifteen, fify, fill, find, fire, first, five, former, formerly, forty, found, four, from, front, full, further, get, give, go, hasnt, have, he, hence, here, hereafter, hereby, herein, hereupon, hers, herself, him, himself, how, however, hundred, ie, if, in, inc, indeed, interest, into, is, it, itself, keep, last, latter, latterly, least, less, ltd, made, many, may, me, meanwhile, might, mill, mine, more, moreover, most, mostly, move, much, must, my, myself, name, namely, neither, never, nevertheless, next, nine, nobody, none, noone, nor, not, nothing, now, nowhere, off, often, once, one, onto, or, other, others, otherwise, our, ours, ourselves, out, over, own, part, per, perhaps, please, put, rather, re, same, see, seem, seemed, seeming, seems, serious, several, should, show, side, since, sincere, six, sixty, so, some, somehow, someone, something, sometime, sometimes, somewhere, still, such, system, take, ten, than, that, the, their, them, themselves, then, thence, there, thereafter, thereby, therefore, therein, thereupon, thickv, thin, third, this, those, though, three, through, throughout, thru, thus, together, too, top, toward, towards, twelve, twenty, two, un, under, until, up, upon, us, very, via, was, we, well, were, what, whatever, when, whence, whenever, where, whereafter, whereas, whereby, wherein, whereupon, wherever, whether, which, while, whither, who, whoever, whole, whom, whose, why, will, with, within, without, yet, you, your, yours, yourself, yourselves, JUST IN, Good Deal, videos, video, HD, say, says, he's, don't, follow, update, updates, just, correction, it's, can't, LIVE BLOG, BREAKING, #Breaking, RT, FULL STORY, photo";
    Set<String> allWordsSet = new HashSet<String>();


    public void init(){
        StringTokenizer st = new StringTokenizer(allWords,",");
        while(st.hasMoreTokens()){
            String s = st.nextToken();
            allWordsSet.add(s.trim().toLowerCase());
        }
    }

    /*public Set<String> findKeyWords(String text){
        StringTokenizer st = new StringTokenizer(allWords,",");
        while(st.hasMoreTokens()){
            String s = st.nextToken();
            allWordsSet.add(s);
        }
    }*/

    public List<String> getKeywords(String text, String src, boolean raw) {
        if(allWordsSet.size() == 0){
            init();
        }
        text = text.toLowerCase();
        if (StringUtils.containsIgnoreCase(text, "Details soon")) {
            text = StringUtils.removeEndIgnoreCase(text, "details");
            text = StringUtils.removeEndIgnoreCase(text, "soon");
        }
        if (StringUtils.containsIgnoreCase(text, "Watch for all the latest details on")) {
            text = text.replaceAll("Watch for all the latest details on".toLowerCase(), "");
        }
        //JUST IN
        if (StringUtils.containsIgnoreCase(text, "JUST IN")) {
            text = text.replaceAll("JUST IN".toLowerCase(), "");
        }
        List<String> keys = new ArrayList<String>();
        //Logic..
        /**
         * 1) Use Pattern compile and split all space words....
         * 2) Ignore all letters less than 2 chars
         * 3) Even chars more than 3
         *
         */
        // Create a pattern to match breaks
        Pattern p = Pattern.compile("[\\s+]");
        // Split input with the pattern
        String[] result = p.split(text);
        for (int i = 0; i < result.length; i++) {
            String s = result[i];
            if (StringUtils.isNotBlank(s)) {
                s = s.trim();

                /* if(StringUtils.endsWith(s,".")){
                    s = s.replaceAll(".","");
                }*/
                if (StringUtils.endsWithIgnoreCase(s, "'s")) {
                    s = s.replaceAll("'s", "");
                }
                /*if(StringUtils.endsWithIgnoreCase(s,",")){
                    s = s.replaceAll(",","");
                }

                if(StringUtils.endsWith(s,":")){
                    s = s.replaceAll(":","");
                }
                if(StringUtils.endsWith(s,"!")){
                    s = s.replaceAll("!","");
                }
                if(StringUtils.endsWith(s,"-")){
                    s = s.replaceAll("-","");
                }
                if(StringUtils.endsWith(s,"|")){
                    s = s.replaceAll("|","");
                }

                if(StringUtils.contains(s,"'")){
                    s = StringUtils.replace(s,"'","");
                }

                if(StringUtils.endsWith(s,"(") || StringUtils.startsWith(s,"(")){
                    s =StringUtils.replace(s,"(","");
                }

                if(StringUtils.endsWith(s,"?") ){
                    s =StringUtils.replace(s,"?","");
                }*/

                if (StringUtils.contains(s, "&rsquo;")) {
                    s = StringUtils.replace(s, "&rsquo;", "");
                }

                /*if(StringUtils.contains(s,"\"")){
                    s =  StringUtils.replace(s,"\"","");
                }

                if(StringUtils.contains(s,"\\")){
                    s =  StringUtils.replace(s,"\\","");
                }

                if(StringUtils.contains(s,"/")){
                    s =  StringUtils.replace(s,"/","");
                }

                if(StringUtils.contains(s,"?")){
                    s =  StringUtils.replace(s,"?","");
                }

                if(StringUtils.contains(s,"<")){
                    s =  StringUtils.replace(s,"<","");
                }

                if(StringUtils.contains(s,">")){
                    s =  StringUtils.replace(s,">","");
                }

                if(StringUtils.endsWith(s,")") || StringUtils.startsWith(s,")")){
                    s = StringUtils.replace(s,")","");
                }*/
                if (StringUtils.isNotBlank(s) && !StringUtils.startsWith(s, "@")) {
                    //all chinese char are missing...shd I care?
                    s = s.replaceAll("[^\\w\\s]", "");
                }

                if (StringUtils.startsWith(s, "@")) {
                    s = "@" + s.replaceAll("[^\\w\\s]", "");
                }

                //TODO: Any special char...ignore..
                if (StringUtils.length(s) > 2) {
                    //this logic is no longer valid.....
                    if (StringUtils.startsWith(s, "@") && StringUtils.length(s) == 3 && (StringUtils.startsWith(s, "-") || StringUtils.startsWith(s, "$"))) {
                        //nothing here..
                    } else if (
                            StringUtils.startsWith(s, "http") || StringUtils.containsIgnoreCase(s, "says") || StringUtils.containsIgnoreCase(s, "#breakingnews")
                                    || allWordsSet.contains(s)) {
                        //nothing..ignore
                    } else {


                        boolean ignore = false;
                        if (StringUtils.isNotBlank(s) && StringUtils.containsIgnoreCase(s, src)) {
                            ignore = true;
                        }

                        if(StringUtils.isNumeric(s)){
                            ignore = true;
                        }

                        if (!ignore && StringUtils.isNotBlank(s)) {
                            if (raw) {
                                if(!keys.contains(s))  {
                                    keys.add(s);
                                }
                            } else {
                                if (StringUtils.startsWith(s, "@") || StringUtils.startsWith(s, "#")) {
                                    if(!keys.contains(s))  {
                                        keys.add(s);
                                    }
                                } else {
                                    if(!keys.contains(s))  {
                                        keys.add(s);
                                    }
                                    //keys.add(aiHelper.stemAWord(s));
                                    //keys.add(StemAWord.stem(s));
                                }
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(text +"---"+keys);
        return keys;
    }
}
