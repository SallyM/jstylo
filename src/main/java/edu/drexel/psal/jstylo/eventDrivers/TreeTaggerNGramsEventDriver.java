/**
 * 
 */
package edu.drexel.psal.jstylo.eventDrivers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgaap.generics.Document;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventDriver;
import com.jgaap.generics.EventSet;

/**
 * This changes words into their parts of speech in a document, based on TreeTagger-a language independent POS tagger.
 * http://www.ims.uni-stuttgart.de/projekte/corplex/TreeTagger/

 * @author sadiaafroz
 *
 */
public class TreeTaggerNGramsEventDriver  extends EventDriver {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(TreeTaggerNGramsEventDriver.class);
	@Override
	public String displayName() {
		return "TreeTagger (a language independent POS tagger) POS N-Grams";
	}

	@Override
	public String tooltipText() {
		return "TreeTagger (a language independent POS tagger)  for POS N-grams";
	}

	@Override
	public boolean showInGUI() {
		return false;
	}

	protected static TreeTaggerWrapper<String> tagger = null;
    protected  String taggerPath = null;
  //path of the tree tagger binary
  	protected  String taggerHome = null;
  	
	@Override
	public EventSet createEventSet(Document doc) {
		EventSet es = new EventSet(doc.getAuthor());
		char[] text = doc.getProcessedText();
		String stringText = new String(text);

		// use TreeTaggerEventDriver tagger
		if (tagger == null) {
			tagger = initTagger();
			if (tagger == null) return es;
		}
		

		final ArrayList<String> tagged = new ArrayList<String>();
		try {

			taggerPath = getParameter("taggerPath");
			tagger.setModel(taggerPath);
			tagger.setHandler(new TokenHandler<String>() {
				public void token(String token, String pos, String lemma) {
					if (!pos.equals("SENT"))
						tagged.add(pos);
				}
			});

			String[] temp = stringText.split("\\s");
			List<String> testList = new ArrayList<String>();
			for (String s : temp)
				testList.add(s);
			tagger.process(testList);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TreeTaggerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i, j, n;
		try {
			n = Integer.parseInt(getParameter("N"));
		} catch (NumberFormatException e) {
			n = 2;
		}
		String curr;
		for (i=0; i<tagged.size()-n+1; i++) {
			curr = "("+tagged.get(i)+")";
			for (j=1; j<n; j++) {
				curr += "-("+tagged.get(i+j)+")";
			}
			es.addEvent(new Event(curr));
		}
		
		return es;
	}
	/**
	 * Initialize the tagger.
	 * @return
	 */
	public TreeTaggerWrapper<String> initTagger() {
		// Point TT4J to the TreeTagger installation directory. The executable is expected
				// in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
		taggerHome = getParameter("taggerHome");
		taggerPath = getParameter("taggerPath");
		LOG.info("taggerHome->"+taggerHome);
				System.setProperty("treetagger.home", taggerHome);
				tagger = new TreeTaggerWrapper<String>();
				return tagger;
	}
	
}
