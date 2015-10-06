package rules;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.scene.image.Image;

import org.slf4j.LoggerFactory;

import schema.SipPreview;
import schema.ui.DescriptionLevelImageCreator;
import schema.ui.SipPreviewNode;
import source.ui.items.SourceTreeDirectory;
import utils.TreeVisitor;
import utils.WalkFileTree;

/**
 * Created by adrapereira on 29-09-2015.
 */
public class Rule extends Observable implements Observer {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Rule.class.getName());
    private SourceTreeDirectory source;
    private RuleTypes type;
    private HashSet<SipPreview> sips;
    private HashSet<SipPreviewNode> sipNodes = new HashSet<SipPreviewNode>();
    private TreeVisitor sipCreator;
    private WalkFileTree treeWalker;
    private Image icon;
    private int sipCount = 0, added = 0;

    public Rule(SourceTreeDirectory source){
        this.source = source;

        ResourceBundle hierarchyConfig = ResourceBundle.getBundle("properties/roda-description-levels-hierarchy");
        String category = hierarchyConfig.getString("category.item");
        String unicode = hierarchyConfig.getString("icon." + category);

        DescriptionLevelImageCreator dlic = new DescriptionLevelImageCreator(unicode);
        icon = dlic.generate();
    }

    public SourceTreeDirectory getSource() {
        return source;
    }

    public void setSource(SourceTreeDirectory source) {
        this.source = source;
    }

    public String getFolderName(){
        return source.getValue().toString();
    }

    public int getSipCount() {
        return sipCount;
    }

    public HashSet<SipPreviewNode> getSipNodes(){return sipNodes;}

    public TreeVisitor apply(RuleTypes type, int level){
        this.type = type;
        sipCount = 0; added = 0;
        sips = new HashSet<SipPreview>();
        TreeVisitor visitor;

        switch (type){
            case SIPPERFILE:
                SipPerFileVisitor visitorFile = new SipPerFileVisitor(source.getPath());
                visitorFile.addObserver(this);
                visitor = visitorFile;
                /*sipCreator = creator;
                treeWalker = new WalkFileTree(source.getPath(), sipCreator);
                treeWalker.start();*/
                break;
            default:
            case SIPPERFOLDER:
                SipPerFolderVisitor visitorFolder = new SipPerFolderVisitor(source.getPath(), level);
                visitorFolder.addObserver(this);
                visitor = visitorFolder;
                /*treeWalker = new WalkFileTree(source.getPath(), sipCreator);
                treeWalker.start();*/
                break;
        }
        return visitor;
    }

    public void update(Observable o, Object arg) {
        if(o instanceof SipPerFileVisitor){
            SipPerFileVisitor visitor = (SipPerFileVisitor) o;
            sipCount = visitor.getCount();
            while(visitor.hasNext() && added < 100){
                added++;
                sipNodes.add(new SipPreviewNode(visitor.getNext(), icon));
            }
        }else if(o instanceof SipPerFolderVisitor){
            SipPerFolderVisitor visitor = (SipPerFolderVisitor) o;
            sipCount = visitor.getCount();
            while(visitor.hasNext() && added < 100) {
                added++;
                sipNodes.add(new SipPreviewNode(visitor.getNext(), icon));
            }
        }
        setChanged();
        notifyObservers();
    }
}
