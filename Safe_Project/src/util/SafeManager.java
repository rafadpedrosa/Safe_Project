package util;

import AST.Access;
import AST.AssignSimpleExpr;
import AST.Block;
import AST.BodyDecl;
import AST.CastExpr;
import AST.ClassDecl;
import AST.ClassInstanceExpr;
import AST.ConstructorDecl;
import AST.EQExpr;
import AST.Expr;
import AST.ExprStmt;
import AST.FieldDeclaration;
import AST.ForStmt;
import AST.IfStmt;
import AST.IntegerLiteral;
import AST.LTExpr;
import AST.List;
import AST.MethodAccess;
import AST.MethodDecl;
import AST.Modifier;
import AST.Modifiers;
import AST.NEExpr;
import AST.NullLiteral;
import AST.Opt;
import AST.ParameterDeclaration;
import AST.PostIncExpr;
import AST.PrimitiveTypeAccess;
import AST.ReturnStmt;
import AST.Stmt;
import AST.SuperConstructorAccess;
import AST.SynchronizedStmt;
import AST.TypeAccess;
import AST.TypeDecl;
import AST.VarAccess;
import AST.BooleanLiteral;
import AST.Dot;
import AST.ClassAccess;
import AST.VariableDeclaration;

public class SafeManager {

	public static boolean classFoiAdicionada = false;
	private static Modifier modPrivate(){return new Modifier("private");}
	private static Modifier modPublic(){return new Modifier("public");}
	private static Modifier modStatic(){return new Modifier("static");}
	private static Modifier modSync(){return new Modifier("synchronized");}
	private static VarAccess instAc(){return new VarAccess("instance");}
	private static VarAccess treeAc(){return new VarAccess("tree");}
	private static VarAccess nodeAc(){return new VarAccess("node");}
	private static VarAccess threadAc() {return new VarAccess("thread");}
	private static MethodAccess methodGetRoot(){
		return new MethodAccess("getRoot",new List<Expr>());}
	private static TypeAccess threadType() {return new TypeAccess("Thread");}
	private static TypeAccess safeType() {return new TypeAccess("SafeNode");}

	public SafeManager() {}

	//private static SafeManager instance;
	//private DefaultTreeModel tree;

	private static void createFields(List<BodyDecl> retorno) {
		Modifiers listaModifiers = new Modifiers();
		listaModifiers.addModifier(modPrivate());

		FieldDeclaration tree = new FieldDeclaration(listaModifiers,
				new TypeAccess("DefaultTreeModel"),"tree");

		listaModifiers.addModifier(modStatic());
		FieldDeclaration instance = new FieldDeclaration(listaModifiers,
				new TypeAccess("SafeManager"),"instance");

		retorno.add(instance);
		retorno.add(tree);

	}

	//	private SafeManager() {
	//		super();
	//		tree = new DefaultTreeModel(null,false);
	//	}

	private static void createConstrutor(List<BodyDecl> retorno) {

		Modifiers listaModifiers = new Modifiers();
		listaModifiers.addModifier(modPrivate());

		Opt<Stmt> opt = new Opt<Stmt>();

		SuperConstructorAccess superConstrutor = 
				new SuperConstructorAccess();
		ExprStmt exprStmt = new ExprStmt(superConstrutor);
		opt.addChild(exprStmt);

		List<Expr> listParameteresClassInstance = new List<Expr>();

		listParameteresClassInstance.add(new NullLiteral("null"));
		listParameteresClassInstance.add(new BooleanLiteral("false"));
		ClassInstanceExpr classInstance = new ClassInstanceExpr(
				new TypeAccess("DefaultTreeModel"),
				listParameteresClassInstance,new Opt<TypeDecl>());

		//		tree = new DefaultTreeModel(null,false);
		AssignSimpleExpr expr = new AssignSimpleExpr(treeAc(),
				classInstance);

		ExprStmt exprStmtTree= new ExprStmt(expr);

		Block block = new Block();
		block.addStmt(exprStmtTree);

		ConstructorDecl contrutor = new ConstructorDecl(
				listaModifiers,"SafeManager",
				new List<ParameterDeclaration>(),new List<Access>(),
				opt,block);

		retorno.add(contrutor);
	}

	//	public static SafeManager getInstance(){
	//		synchronized(SafeManager.class){
	//			if (instance == null) {
	//				instance = new SafeManager();			
	//			}
	//		}
	//		return instance;
	//	}

	private static void createGetInstance(List<BodyDecl> retorno) {

		Block mainBlock = new Block();

		Modifiers listaModifiers = new Modifiers();
		TypeAccess typeAccess = new TypeAccess("SafeManager");

		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modStatic());

		Dot dot = new Dot(typeAccess,new ClassAccess());

		SynchronizedStmt syncStmt = new SynchronizedStmt(
				dot,createBlocoIfGetInstance());

		mainBlock.addStmt(syncStmt);

		ReturnStmt returnStmt = new ReturnStmt(instAc());
		mainBlock.addStmt(returnStmt);

		Opt<Block> opt = new Opt<Block>();

		opt.addChild(mainBlock);

		MethodDecl method = new MethodDecl(listaModifiers,
				typeAccess,"getInstance",
				new List<ParameterDeclaration>(),
				new List<Access>(),opt);

		retorno.add(method);
	}

	//synchronized(SafeManager.class){
	//			if (instance == null) {
	//				instance = new SafeManager();			
	//			}
	//		}

	private static Block createBlocoIfGetInstance() {
		Block blocoIf = new Block();

		ClassInstanceExpr classInstance = 
				new ClassInstanceExpr(
						new TypeAccess("SafeManager"),
						new List<Expr>(),new Opt<TypeDecl>());

		AssignSimpleExpr assignSimple = new AssignSimpleExpr(
				instAc(),classInstance);

		ExprStmt assignStmt = new ExprStmt(assignSimple);

		blocoIf.addStmt(assignStmt);

		EQExpr eqExpr = new EQExpr(instAc(),
				new NullLiteral("null"));
		IfStmt ifStmt = new IfStmt(eqExpr,blocoIf);
		Block retorno = new Block();
		retorno.addStmt(ifStmt);
		return retorno;
	}

	//classname SafeManager {...}

	public static  List<TypeDecl> 
	createAndAddClassDecl(List<TypeDecl> list){

		Opt<Access> opt = new Opt<Access>();
		List<Access> listaAccess = new List<Access>();
		List<BodyDecl> listaBodyDecl = createListBodyDecl();
		list.add(new ClassDecl(new Modifiers(),"SafeManager",
				opt,listaAccess,listaBodyDecl));

		return list;
	}

	private static List<BodyDecl> createListBodyDecl() {

		List<BodyDecl> retorno = new List<BodyDecl>();

		createFields(retorno);
		createConstrutor(retorno);
		createGetInstance(retorno);
		createAddSafe(retorno);
		createGetRoot(retorno);
		createGetTree(retorno);
		createRemoveSafe(retorno);
		createIsSafe(retorno);
		createGetSafe(retorno);

		return retorno;
	}
	//	public synchronized void addSafe(SafeNode newChild,SafeNode parent){
	//		if(tree.getRoot() == null){
	//			tree.setRoot(newChild);
	//		}
	//		else{
	//			tree.insertNodeInto(newChild,(MutableTreeNode) parent, 0);
	//		}
	//	}

	private static void createAddSafe(List<BodyDecl> retorno) {

		Modifiers listaModifiers = new Modifiers();

		PrimitiveTypeAccess primitiveAccess = 
				new PrimitiveTypeAccess("void");

		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modSync());			

		Block principalBloco = new Block();

		IfStmt ifStmt = createIfStmtAddSafe();
		principalBloco.addStmt(ifStmt);

		Opt<Block> opt = new Opt<Block>();
		opt.addChild(principalBloco);

		List<ParameterDeclaration> parameters = 
				new List<ParameterDeclaration>();
		parameters.add(new ParameterDeclaration(
				safeType(),"newChild"));
		parameters.add(new ParameterDeclaration(
				safeType(),"parent"));

		MethodDecl method = new MethodDecl(listaModifiers,
				primitiveAccess,"addSafe",parameters,
				new List<Access>(),opt);

		retorno.add(method);
	}
	//	public synchronized SafeNode getRoot(){
	//	return (SafeNode) tree.getRoot();
	//}

	private static void createGetRoot(List<BodyDecl> retorno) {
		Modifiers listaModifiers = new Modifiers();
		TypeAccess returnType = safeType();

		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modSync());			

		Block principalBloco = new Block();

		principalBloco.addStmt(new ReturnStmt(new CastExpr(
				safeType(),new Dot(treeAc(),methodGetRoot()))));

		Opt<Block> opt = new Opt<Block>();
		opt.addChild(principalBloco);

		List<ParameterDeclaration> parameters = 
				new List<ParameterDeclaration>();

		MethodDecl method = new MethodDecl(listaModifiers,
				returnType,"getRoot",parameters	,
				new List<Access>(),opt);

		retorno.add(method);
	}
	//public synchronized DefaultTreeModel getTree(){
	//	return tree;
	//}

	private static void createGetTree(List<BodyDecl> retorno) {
		Modifiers listaModifiers = new Modifiers();

		TypeAccess returnType = 
				new TypeAccess("DefaultTreeModel");

		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modSync());			

		Block principalBloco = new Block();

		principalBloco.addStmt(new ReturnStmt(treeAc()));

		Opt<Block> opt = new Opt<Block>();
		opt.addChild(principalBloco);

		List<ParameterDeclaration> parameters = 
				new List<ParameterDeclaration>();

		MethodDecl method = new MethodDecl(listaModifiers,
				returnType,"getTree",parameters,
				new List<Access>(),opt);

		retorno.add(method);
	}
	//		if(tree.getRoot() == null){
	//			tree.setRoot(newChild);
	//		}
	//		else{
	//			tree.insertNodeInto(newChild,(MutableTreeNode) parent, 0);
	//		}

	private static IfStmt createIfStmtAddSafe() {
		Block blockIf = new Block();
		List<Expr> parametersIf = new List<Expr>();
		parametersIf.add(new VarAccess("newChild"));

		//tree.setRoot(newChild);
		ExprStmt exprStmtIf = new ExprStmt(new Dot(treeAc(),
				new MethodAccess("setRoot",parametersIf )));
		blockIf.addStmt(exprStmtIf);
		//tree.getRoot() == null
		EQExpr eqExpr = new EQExpr(new Dot(treeAc(),methodGetRoot()),
				new NullLiteral("null"));

		Opt<Stmt> opt = new Opt<Stmt>();
		Block blockElse = new Block();

		List<Expr> parametersElse = new List<Expr>();
		VarAccess varAccess = new VarAccess("newChild");
		//(MutableTreeNode) parent
		CastExpr castExpr = new CastExpr(
				new TypeAccess("MutableTreeNode"),new VarAccess("parent"));
		IntegerLiteral integerLiteral = new IntegerLiteral(0);
		parametersElse.add(varAccess);
		parametersElse.add(castExpr);
		parametersElse.add(integerLiteral);

		//.insertNodeInto(newChild,(MutableTreeNode) parent, 0);
		MethodAccess methodAccess = new MethodAccess(
				"insertNodeInto",parametersElse);

		//tree.insertNodeInto(newChild,(MutableTreeNode) parent, 0);	
		ExprStmt exprStmtElse = new ExprStmt(new Dot(
				treeAc(),methodAccess));

		blockElse.addStmt(exprStmtElse);

		opt.addChild(blockElse);
		return new IfStmt(eqExpr,blockIf,opt);
	}
	//	public synchronized void removeSafe(SafeNode safeNode){
	//		if(tree.getRoot() != null){
	//			tree.removeNodeFromParent(safeNode);
	//		}
	//		else if(tree.getRoot().equals(safeNode)){
	//			tree.setRoot(null);
	//		}
	//	}

	private static void createRemoveSafe(List<BodyDecl> retorno) {
		Modifiers listaModifiers = new Modifiers();

		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modSync());

		Block mainBlock = new Block();

		//if(tree.getRoot() != null){...}
		IfStmt ifStmt = createIfStmtRemoveSafe();

		mainBlock.addStmt(ifStmt);
		Opt<Block> opt = new Opt<Block>();
		opt.addChild(mainBlock);

		List<ParameterDeclaration> parameters =
				new List<ParameterDeclaration>();

		parameters.add(new ParameterDeclaration(
				safeType(),"safeNode"));

		MethodDecl method = new MethodDecl(listaModifiers,
				new PrimitiveTypeAccess("void"),"removeSafe",
				parameters,new List<Access>(),opt);

		retorno.add(method);
	}
	//if(tree.getRoot() != null){
	//			tree.removeNodeFromParent(safeNode);
	//		}
	//		else if(tree.getRoot().equals(safeNode)){
	//			tree.setRoot(null);
	//		}

	private static IfStmt createIfStmtRemoveSafe() {
		Block blockIf = new Block();

		VarAccess safeNodeAccess = new VarAccess("safeNode");
		List<Expr> parametersIf = new List<Expr>();
		parametersIf.add(safeNodeAccess);

		//tree.removeNodeFromParent(safeNode);		
		ExprStmt exprStmt = new ExprStmt(new Dot(treeAc(),
				new MethodAccess("removeNodeFromParent",
						parametersIf)));

		blockIf.addStmt(exprStmt);
		//tree.getRoot() != null
		NEExpr neExpr = new NEExpr(new Dot(treeAc(),
				methodGetRoot()),new NullLiteral("null"));

		Opt<Stmt> opt = new Opt<Stmt>();
		Block blockElse = new Block();

		List<Expr> parametersElse = new List<Expr>();
		parametersElse.add(safeNodeAccess);

		//tree.getRoot().equals(safeNode)
		Dot dotInt = new Dot(treeAc(),new Dot(
				methodGetRoot(),new MethodAccess(
						"equals",parametersElse)));

		List<Expr> parameters2 = new List<Expr>();
		NullLiteral nullLiteral = new NullLiteral("null");
		parameters2.add(nullLiteral);

		//		else if(tree.getRoot().equals(safeNode)){
		//			tree.setRoot(null);
		//		}

		IfStmt ifStmtElse = new IfStmt(dotInt,new ExprStmt(
				new Dot(treeAc(),new MethodAccess(
						"setRoot",parameters2))));

		blockElse.addStmt(ifStmtElse);

		opt.addChild(blockElse);
		return new IfStmt(neExpr,blockIf,opt);
	}
	//	public synchronized boolean isSafe(Thread thread){
	//		if(getSafe((SafeNode) tree.getRoot(),thread) != null){
	//			return true;
	//		}
	//		return false;
	//	}

	private static void createIsSafe(List<BodyDecl> retorno) {
		Block mainBlock = new Block();

		Modifiers listaModifiers = new Modifiers();
		listaModifiers.addModifier(modPublic());
		listaModifiers.addModifier(modSync());

		List<ParameterDeclaration> parameters = 
				new List<ParameterDeclaration>();

		parameters.add(new ParameterDeclaration(
				threadType(),"thread"));

		List<Expr> getSafeParameters = new List<Expr>();
		CastExpr castExpr = new CastExpr(safeType(),new Dot(
				treeAc(),methodGetRoot()));
		getSafeParameters.add(castExpr);
		getSafeParameters.add(threadAc());

		NEExpr neExpr = new NEExpr(new MethodAccess("getSafe",
				getSafeParameters),new NullLiteral("null"));

		Block blockIf = new Block();

		ReturnStmt returnStmtTrue =	
				new ReturnStmt(new BooleanLiteral(true));

		blockIf.addStmt(returnStmtTrue);

		IfStmt ifStmt = new IfStmt(neExpr,blockIf);
		mainBlock.addStmt(ifStmt);

		ReturnStmt returnStmtFalse = 
				new ReturnStmt(new BooleanLiteral(false));
		mainBlock.addStmt(returnStmtFalse);

		Opt<Block> opt = new Opt<Block>();
		opt.addChild(mainBlock);

		MethodDecl method = new MethodDecl(listaModifiers,
				new PrimitiveTypeAccess("boolean"),
				"isSafe",parameters,new List<Access>(), 
				opt);

		retorno.add(method);
	}
	//	public SafeNode getSafe(SafeNode originalNode,Thread thread){
	//		if(originalNode == null){
	//			return null;
	//		}
	//		else if(originalNode.existsThread(thread)){
	//			return (SafeNode) originalNode;
	//		}
	//		SafeNode node = (SafeNode) tree.getChild(originalNode,0);
	//		for (int i = 0; i < tree.getChildCount(originalNode); i++) {
	//
	//			node = (SafeNode) tree.getChild(originalNode, i);
	//			if(node.existsThread(thread)){
	//				return node;
	//			}
	//		}
	//		return getSafe(node,thread);
	//	}

	private static void createGetSafe(List<BodyDecl> retorno) {

		Modifiers listaModifiers = new Modifiers();
		listaModifiers.addModifier(modPublic());
		Block mainBlock = new Block();

		List<ParameterDeclaration> parameters = 
				new List<ParameterDeclaration>();

		parameters.add(new ParameterDeclaration(safeType(),
				"originalNode"));
		parameters.add(new ParameterDeclaration(
				threadType(),"thread"));

		IfStmt ifOriginalIsNull = getIfStmtGetSafe();

		mainBlock.addStmt(ifOriginalIsNull);

		VariableDeclaration setNode = getSetNode();
		mainBlock.addStmt(setNode);

		List<Stmt> listVariableInitFor = new List<Stmt>();
		
		VariableDeclaration variableFor = new VariableDeclaration(
				new PrimitiveTypeAccess("int"),"i",
				new IntegerLiteral(0));

		listVariableInitFor.add(variableFor);

		ForStmt forStmt = new ForStmt(listVariableInitFor,
				getListCondictionFor(),getListDoFor(),getBlockFor());

		mainBlock.addStmt(forStmt);

		List<Expr> returnParameters = new List<Expr>();
		returnParameters.add(nodeAc());
		returnParameters.add(threadAc());
		ReturnStmt returnStmt = new ReturnStmt(
				new MethodAccess("getSafe",returnParameters));

		mainBlock.addStmt(returnStmt);

		Opt<Block> mainOpt = new Opt<Block>();
		mainOpt.addChild(mainBlock);

		MethodDecl method = new MethodDecl(listaModifiers,
				safeType(),"getSafe",parameters,
				new List<Access>(),mainOpt);

		retorno.add(method);
	}
	/**
	 * method to createGetSafe.
	 * 
	 * @return
	 */
	private static Block getBlockFor() {
		Block blockFor = new Block();

		List<Expr> assignParameters = new List<Expr>();
		assignParameters.add(new VarAccess("originalNode"));
		assignParameters.add(new VarAccess("i"));
		AssignSimpleExpr assignNode = new AssignSimpleExpr(
				nodeAc(),new CastExpr(safeType(),
						new Dot(treeAc(),new MethodAccess(
								"getChild",assignParameters))));
		ExprStmt setNodeFor = new ExprStmt(assignNode);
		blockFor.addStmt(setNodeFor);
		List<Expr> ifForParameters = new List<Expr>();
		ifForParameters.add(threadAc());

		Block blockIfFor = new Block();
		blockIfFor.addStmt(new ReturnStmt(nodeAc()));

		IfStmt ifFor = new IfStmt(new Dot(nodeAc(),
				new MethodAccess("existsThread",ifForParameters)),
				blockIfFor);

		blockFor.addStmt(ifFor);
		return blockFor;
	}
	/**
	 * method to createGetSafe.
	 * 
	 * @return
	 */
	private static List<Stmt> getListDoFor() {
		List<Stmt> listDoFor = new List<Stmt>();
		ExprStmt exprStmt = 
				new ExprStmt(new PostIncExpr(new VarAccess("i")));
		listDoFor.add(exprStmt);
		return listDoFor;
	}
	/**
	 * method to createGetSafe.
	 * 
	 * @return
	 */
	private static Opt<Expr> getListCondictionFor() {
		Opt<Expr> listCondictionFor = new Opt<Expr>();
		List<Expr> parametersCondictionFor = new List<Expr>();
		parametersCondictionFor.add(new VarAccess("originalNode"));
		LTExpr ltExpr = new LTExpr(new VarAccess("i"),new Dot(
				treeAc(),new MethodAccess("getChildCount",
						parametersCondictionFor)));
		listCondictionFor.addChild(ltExpr);
		return listCondictionFor;
	}
	/**
	 * method to createGetSafe.
	 * 
	 * @return
	 */
	private static VariableDeclaration getSetNode() {
		Opt<Expr> opt = new Opt<Expr>();
		List<Expr> list = new List<Expr>();
		list.add(new VarAccess("originalNode"));
		list.add(new IntegerLiteral(0));

		opt.addChild(new CastExpr(safeType(),new Dot(
				treeAc(),new MethodAccess("getChild",list))));

		return new VariableDeclaration(
				new Modifiers(),safeType(),"node",opt);
	}
	//if(originalNode == null){
	//			return null;
	//		}
	//		else if(originalNode.existsThread(thread)){
	//			return (SafeNode) originalNode;
	//		}

	private static IfStmt getIfStmtGetSafe() {
		Block blockIfOriginalElse = new Block();

		//return (SafeNode) originalNode;
		ReturnStmt returnElse = new ReturnStmt(new CastExpr(
				safeType(),new VarAccess("originalNode")));

		blockIfOriginalElse.addStmt(returnElse);

		List<Expr> parametersIfOriginalElse = new List<Expr>();
		parametersIfOriginalElse.add(threadAc());

		//else if(originalNode.existsThread(thread)){...}

		IfStmt ifOriginalElse = new IfStmt(new Dot(
				new VarAccess("originalNode"),
				new MethodAccess("existsThread",parametersIfOriginalElse)),
				blockIfOriginalElse);

		Opt<Stmt> elseStmt = new Opt<Stmt>();
		elseStmt.addChild(ifOriginalElse);

		Block blockOriginalNull = new Block();

		//return null;
		ReturnStmt returnOriginalNull = 
				new ReturnStmt(new NullLiteral("null"));
		blockOriginalNull.addStmt(returnOriginalNull);


		EQExpr eqExpr = new EQExpr(new VarAccess("originalNode"),
				new NullLiteral("null"));

		//if(originalNode == null){...}
		return new IfStmt(eqExpr,blockOriginalNull,elseStmt);
	}

}