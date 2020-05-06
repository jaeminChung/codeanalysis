package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaStatement;
import org.eclipse.jdt.core.dom.*;
import org.jboss.logging.Logger;


public class StatementProcessor extends ASTVisitor {
    final static Logger logger = Logger.getLogger(StatementProcessor.class.getName());
    final private JavaMethod method;

    public StatementProcessor(JavaMethod method) {
        this.method = method;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        addStatement(node);

        return super.visit(node);
    }

    private void addStatement(ASTNode node) {
        JavaStatement statement = new JavaStatement();
        statement.setJavaMethod(method);
        method.addStatement(statement);

        statement.setStartPos(node.getStartPosition());
        statement.setLength(node.getLength());
        statement.setStatement(node.toString());

        //node.visit(new MethodCallProcessor(javaStatement));
    }

    @Override
    public boolean visit(ForStatement node) {
        if( node.initializers() != null ) {
            for( Object initializer : node.initializers() ) {
                if( initializer instanceof ASTNode ) {
                    ASTNode condNode = (ASTNode) initializer;
                    logger.info( String.format("  For statement initializer : %s", condNode.toString()) );
                }
            }
        }

        if( node.getExpression() != null ) {
            ASTNode condNode = (ASTNode) node.getExpression();
            logger.info( String.format("  For statement expression : %s", condNode.toString()) );
        }

        if( node.updaters() != null ) {
            for( Object updater : node.updaters() ) {
                if( updater instanceof  ASTNode ) {
                    ASTNode condNode = (ASTNode) updater;
                    logger.info( String.format("  For statement updaters : %s", condNode.toString()) );
                }
            }
        }

        if( node.getBody() != null ) {
            node.getBody().accept( this );
        }
        return false;
    }

    @Override
    public boolean visit(IfStatement node) {
        if( node.getExpression() != null ) {
            logger.info( String.format("  If statement expression : %s", node.getExpression().toString()) );
            node.getExpression().accept( this );
        }

        if( node.getThenStatement() != null ) {
            node.getThenStatement().accept( this );
        }

        if( node.getElseStatement() != null ) {
            node.getElseStatement().accept( this );
        }

        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
        Expression expression = node.getExpression();

        if( expression != null ) {
            logger.info( String.format("  Do statement expression : %s", expression.toString()) );
        }

        if( node.getBody() != null ) {
            node.getBody().accept( this );
        }

        return false;
    }

    @Override
    public boolean visit(WhileStatement node) {
        Expression expression = node.getExpression();

        if( expression != null ) {
            logger.info( String.format("  While statement expression : %s", expression.toString()) );
        }

        if( node.getBody() != null ) {
            node.getBody().accept( this );
        }

        return false;
    }

    @Override
    public  boolean visit(SwitchStatement node) {
        Expression expression = node.getExpression();

        if( expression != null ) {
            logger.info( String.format("  Switch statement expression : %s", expression.toString()) );
        }

        for( int i=0; (node.statements() != null) && (i < node.statements().size()); i++) {
            Statement stmtNode = (Statement) node.statements().get(i);
            if( stmtNode instanceof  SwitchCase ) {
                SwitchCase caseNode = (SwitchCase) stmtNode;

                if( caseNode.getExpression() != null ) {
                    logger.info( String.format("  Switch statement case expression : %s", caseNode.getExpression().toString()) );
                    caseNode.getExpression().accept( this );
                }
            } else {
                stmtNode.accept( this );
            }
        }

        return false;
    }
}
