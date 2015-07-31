package Extractor;
import java.util.ArrayList;


public class StopWords  {

public static String[] stopWordsofwordnet = {"without", "see", "unless", "due", "also", "must", "might", "like", "]", "[", "}", "{", "<", ">", "?", "\"", "\\", "/", ")", "(",
"Without", "See", "Unless", "Due", "Also", "Must", "Might", "Like", "Will", "May", "Can", "Much", "Every", "The", "In", "Other", "This", "The", "Many", "Any", "An", "Or", "For", "In", "An", "An ", "Is", "A", "About", "Above", "After", "Again", "Against", "All", "Am", "An", "Any", "Are", "Aren’t", "As", "At", "Be", "Because", "Been", "Before", "Being", "Below", "Between", "Both", "But", "By", "Can’t", "Cannot", "Could",
"Couldn’t", "Do", "Does", "Doesn’t", "Doing", "Don’t", "Down", "During", "Each", "Few", "For", "From", "Further", "Had", "Hadn’t", "Has", "Hasn’t", "Have", "Haven’t", "Having",
"He", "He’d", "He’ll", "Her", "Here", "Here’s", "Hers", "Herself", "Him", "Himself", "How", "How’s", "I ", " I", "I’d", "I’ll", "I’m", "I’ve", "If", "In", "Into", "Is",
"Isn’t", "It", "It’s", "Its", "Itself", "Let’s", "Me", "More", "Most", "Mustn’t", "My", "Myself", "No", "Nor", "Not", "On", "Once", "Only", "Ought", "Our", "Ours", "Ourselves",
"Out", "Over", "Own", "Same", "Shan’t", "She", "She’d", "She’ll", "She’s", "Should", "Shouldn’t", "So", "Some", "Such", "Than",
"That", "That’s", "Their", "Theirs", "Them", "Themselves", "Then", "There", "There’s", "They", "They’d", "They’ll", "They’re", "They’ve",
"This", "Those", "Through", "To", "Too", "Under", "Until", "Up", "Very", "Was", "Wasn’t", "We", "We’d", "We’ll", "We’re", "We’ve",
"Were", "Weren’t", "What", "What’s", "When", "When’s", "Where", "Where’s", "Which", "While", "Who", "Who’s", "Whom",
"Why", "Why’s", "©","With", "Won’t", "Would", "Wouldn’t", "You", "You’d", "You’ll", "You’re", "You’ve", "Your", "Yours", "Yourself", "Yourselves","=",".","|","\"","Yes"};


public String removeStopword(String line)
{

line=line.trim().replaceAll("\\s+", " ");
String[] words = line.split(" ");
ArrayList<String> wordsList = new ArrayList<String>();

for (String word : words) {
wordsList.add(word);
}
try
{
//remove stop words here from the temp list
for (int i = 0; i < wordsList.size(); i++) {
// get the item as string
for (int j = 0; j < stopWordsofwordnet.length; j++) {
if (stopWordsofwordnet[j].toLowerCase().contains(wordsList.get(i).toLowerCase())) {

//System.out.println(wordsList.get(i));
wordsList.remove(i);
}
}
}
}catch(Exception e)
{}
StringBuilder listString = new StringBuilder();

for (String str : wordsList) {
//System.out.print(str+" ");
	if (!str.matches(".*\\d+.*"))
		
	{
		if(!((str.length())>20))

			listString.append(str+" ");
		
	}
}
return listString.toString();
}

}