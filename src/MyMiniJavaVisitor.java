import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMiniJavaVisitor extends MiniJavaBaseVisitor<Tree> {
    private Parser parser;

    public MyMiniJavaVisitor(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Tree visit(ParseTree tree) {
        return visit(tree, null);
    }

    public Tree visit(ParseTree tree, Tree parent) {
        Parser parser = this.parser;
        List<Tree> children = new ArrayList<>();

        Tree res = new Tree() {
            @Override
            public Tree getParent() {
                return parent;
            }

            @Override
            public Object getPayload() {
                return getName(tree);
            }

            @Override
            public Tree getChild(int i) {
                return children.get(i);
            }

            @Override
            public int getChildCount() {
                return children.size();
            }

            @Override
            public String toStringTree() {
                return null;
            }
        };

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            while (getName(child).startsWith("Next")) {
                child = child.getChild(0);
            }
            if (!(child instanceof TerminalNodeImpl)) {
                children.add(visit(child, res));
            }
        }

        return res;
    }

    private String getName(Tree tree) {
        Pattern p = Pattern.compile("\\w+\\$(\\w+)Context");
        Matcher m = p.matcher(tree.getClass().getName());
        if (m.matches()) {
            return m.group(1);
        }
        else {
            return tree.getClass().getName();
        }
    }
}