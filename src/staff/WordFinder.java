package staff;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

/**
 * WordFinder is an interface for searching a word list.
 * When the user types any part of a word, the interface displays all the words
 * that match.
 */
public class WordFinder extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private final WordSet words = new WordSet();
    
    private final JList<String> list;
    private final DefaultListModel<String> model;
    
    private final JTextField find;
    
    /**
     * Create a WordFinder window.
     */
    public WordFinder() {
        super("Word Finder");
        
        // call System.exit() when user closes the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Container cp = this.getContentPane();
        
        // sets the layout manager of the content pane to a GroupLayout
        GroupLayout layout = new GroupLayout(cp);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        cp.setLayout(layout);
        
        // create label and text field
        JLabel findLabel = new JLabel("Find: ");
        find = new JTextField(20);
        
        /*
         * Add an ActionListener to `find` that prints matching words to the console.
         */
        find.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFind();
            }
        });
        
        // create matching-words-list inside a scroll pane
        model = new DefaultListModel<>();
        list = new JList<>(model);
        JScrollPane scroller = new JScrollPane(list);
        
        // label and text field are in sequence horizontally
        SequentialGroup h1 = layout.createSequentialGroup();
        h1.addComponent(findLabel);
        h1.addComponent(find);
        
        // label and text field are in parallel vertically, aligned at text baseline
        ParallelGroup v1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v1.addComponent(findLabel);
        v1.addComponent(find);
        
        // create horizontal & vertical groups for the list (inside the scroller)
        SequentialGroup v2 = layout.createSequentialGroup().addComponent(scroller);
        SequentialGroup h2 = layout.createSequentialGroup().addComponent(scroller);
        
        // (label + text field) and list are in parallel horizontally
        ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hGroup.addGroup(h1).addGroup(h2);
        layout.setHorizontalGroup(hGroup);
        
        // (label + text field) and list are in sequence vertically
        SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(v1).addGroup(v2);
        layout.setVerticalGroup(vGroup);
        
        // size the window to fit the default sizes of all the components
        this.pack();
    }
    
    /**
     * Load the word set from a file.
     * @throws IOException if there is an error loading the word list
     */
    private void loadWords() throws IOException {
        InputStream in = new FileInputStream("words");
        words.load(in);
    }
    
    /**
     * Query the word set with the current input and display results in the list.
     */
    private void doFind() {
        // (1) String query = find.getText();
        
        // (2) List<String> matches = words.find(query);
        // (3) model.removeAllElements();
        //     for (String match : matches) {
        //         model.addElement(match);
        //     }
        
        // (4) find.selectAll();
        //     find.grabFocus();
        
        String query = find.getText(); // (1)
        new FindWorker(query).execute();
    }
    
    class FindWorker extends SwingWorker<List<String>, Object> {
        
        private final String query;
        
        public FindWorker(String query) {
            this.query = query;
        }
        
        @Override
        protected List<String> doInBackground() throws Exception {
            // what thread are we on now?
            return words.find(query); // (2)
        }   
        
        @Override
        protected void done() {
            // what thread are we on now?
            
            // (3)
            List<String> matches;
            try {
                matches = this.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(WordFinder.this,
                                              e.getMessage(),
                                              "Error searching words",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
            model.removeAllElements();
            for (String match : matches) {
                model.addElement(match);
            }
            
            // (4)
            find.selectAll();
            find.grabFocus();
        }
    }
    
    /**
     * Create and display a WordFinder window.
     * @param args command-line arguments, ignored
     */
    public static void main(String[] args) {
        
        /*
         * Swing objects may only be accessed from the event-handling thread,
         * not from the main thread or other threads you create yourself.
         * SwingUtilities.invokeLater() asynchronously invokes a Runnable's
         * run() method on the event-handling thread.
         */
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                
                // create and display the WordFinder window
                WordFinder window = new WordFinder();
                window.setVisible(true);
                
                // load the word set
                try {
                    window.loadWords();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(window,
                                                  ioe.getMessage(),
                                                  "Error loading word file",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
