// Generated from /home/gabi/Projects/kotlin/jplank/parser/src/commonAntlr/antlr/PlankParser.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PlankParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, NEWLINE=2, AT=3, SEMICOLON=4, COMMA=5, COLON=6, BAR=7, LPAREN=8, 
		RPAREN=9, LBRACE=10, RBRACE=11, LBRACKET=12, RBRACKET=13, APOSTROPHE=14, 
		DOT=15, AMPERSTAND=16, ADD=17, SUB=18, DIV=19, TIMES=20, CONCAT=21, BANG=22, 
		EQUAL=23, ASSIGN=24, GT=25, LT=26, GTE=27, LTE=28, EQ=29, NEQ=30, DOUBLE_ARROW_LEFT=31, 
		ARROW_LEFT=32, RETURN=33, FUN=34, TYPE=35, LET=36, IF=37, ELSE=38, MUTABLE=39, 
		TRUE=40, FALSE=41, IMPORT=42, SIZEOF=43, MODULE=44, MATCH=45, CASE=46, 
		IDENTIFIER=47, STRING=48, INT=49, DECIMAL=50;
	public static final int
		RULE_file = 0, RULE_module = 1, RULE_semis = 2, RULE_ws = 3, RULE_qualifiedPath = 4, 
		RULE_typeRef = 5, RULE_typePrimary = 6, RULE_param = 7, RULE_decl = 8, 
		RULE_enumMember = 9, RULE_prop = 10, RULE_attr = 11, RULE_attrExpr = 12, 
		RULE_stmt = 13, RULE_pattern = 14, RULE_expr = 15, RULE_instanceArg = 16, 
		RULE_arg = 17, RULE_matchPattern = 18, RULE_primary = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "module", "semis", "ws", "qualifiedPath", "typeRef", "typePrimary", 
			"param", "decl", "enumMember", "prop", "attr", "attrExpr", "stmt", "pattern", 
			"expr", "instanceArg", "arg", "matchPattern", "primary"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'@'", "';'", "','", "':'", "'|'", "'('", "')'", "'{'", 
			"'}'", "'['", "']'", "'''", "'.'", "'&'", "'+'", "'-'", "'/'", "'*'", 
			null, "'!'", "'='", null, "'>'", "'<'", null, null, null, null, null, 
			null, "'return'", "'fun'", "'type'", "'let'", "'if'", "'else'", "'mutable'", 
			"'true'", "'false'", "'import'", "'sizeof'", "'module'", "'match'", "'case'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "NEWLINE", "AT", "SEMICOLON", "COMMA", "COLON", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "APOSTROPHE", "DOT", 
			"AMPERSTAND", "ADD", "SUB", "DIV", "TIMES", "CONCAT", "BANG", "EQUAL", 
			"ASSIGN", "GT", "LT", "GTE", "LTE", "EQ", "NEQ", "DOUBLE_ARROW_LEFT", 
			"ARROW_LEFT", "RETURN", "FUN", "TYPE", "LET", "IF", "ELSE", "MUTABLE", 
			"TRUE", "FALSE", "IMPORT", "SIZEOF", "MODULE", "MATCH", "CASE", "IDENTIFIER", 
			"STRING", "INT", "DECIMAL"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PlankParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PlankParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FileContext extends ParserRuleContext {
		public ModuleContext module() {
			return getRuleContext(ModuleContext.class,0);
		}
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(40);
				module();
				}
				break;
			}
			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << FUN) | (1L << TYPE) | (1L << LET) | (1L << IMPORT) | (1L << MODULE))) != 0)) {
				{
				{
				setState(43);
				decl();
				}
				}
				setState(48);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleContext extends ParserRuleContext {
		public QualifiedPathContext path;
		public TerminalNode MODULE() { return getToken(PlankParser.MODULE, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public QualifiedPathContext qualifiedPath() {
			return getRuleContext(QualifiedPathContext.class,0);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_module);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(MODULE);
			setState(50);
			((ModuleContext)_localctx).path = qualifiedPath();
			setState(51);
			semis();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SemisContext extends ParserRuleContext {
		public TerminalNode SEMICOLON() { return getToken(PlankParser.SEMICOLON, 0); }
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public SemisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_semis; }
	}

	public final SemisContext semis() throws RecognitionException {
		SemisContext _localctx = new SemisContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_semis);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(SEMICOLON);
			setState(54);
			ws();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WsContext extends ParserRuleContext {
		public List<TerminalNode> SEMICOLON() { return getTokens(PlankParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(PlankParser.SEMICOLON, i);
		}
		public List<TerminalNode> WS() { return getTokens(PlankParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PlankParser.WS, i);
		}
		public WsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ws; }
	}

	public final WsContext ws() throws RecognitionException {
		WsContext _localctx = new WsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_ws);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS || _la==SEMICOLON) {
				{
				{
				setState(56);
				_la = _input.LA(1);
				if ( !(_la==WS || _la==SEMICOLON) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QualifiedPathContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(PlankParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(PlankParser.IDENTIFIER, i);
		}
		public List<TerminalNode> DOT() { return getTokens(PlankParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(PlankParser.DOT, i);
		}
		public QualifiedPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedPath; }
	}

	public final QualifiedPathContext qualifiedPath() throws RecognitionException {
		QualifiedPathContext _localctx = new QualifiedPathContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_qualifiedPath);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(IDENTIFIER);
			setState(67);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(63);
					match(DOT);
					setState(64);
					match(IDENTIFIER);
					}
					} 
				}
				setState(69);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeRefContext extends ParserRuleContext {
		public TypeRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeRef; }
	 
		public TypeRefContext() { }
		public void copyFrom(TypeRefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrimaryTypeRefContext extends TypeRefContext {
		public TypePrimaryContext value;
		public TypePrimaryContext typePrimary() {
			return getRuleContext(TypePrimaryContext.class,0);
		}
		public PrimaryTypeRefContext(TypeRefContext ctx) { copyFrom(ctx); }
	}
	public static class FunctionTypeRefContext extends TypeRefContext {
		public TypeRefContext parameter;
		public TypeRefContext returnType;
		public TerminalNode ARROW_LEFT() { return getToken(PlankParser.ARROW_LEFT, 0); }
		public List<TypeRefContext> typeRef() {
			return getRuleContexts(TypeRefContext.class);
		}
		public TypeRefContext typeRef(int i) {
			return getRuleContext(TypeRefContext.class,i);
		}
		public FunctionTypeRefContext(TypeRefContext ctx) { copyFrom(ctx); }
	}

	public final TypeRefContext typeRef() throws RecognitionException {
		return typeRef(0);
	}

	private TypeRefContext typeRef(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeRefContext _localctx = new TypeRefContext(_ctx, _parentState);
		TypeRefContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_typeRef, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new PrimaryTypeRefContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(71);
			((PrimaryTypeRefContext)_localctx).value = typePrimary();
			}
			_ctx.stop = _input.LT(-1);
			setState(78);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new FunctionTypeRefContext(new TypeRefContext(_parentctx, _parentState));
					((FunctionTypeRefContext)_localctx).parameter = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_typeRef);
					setState(73);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(74);
					match(ARROW_LEFT);
					setState(75);
					((FunctionTypeRefContext)_localctx).returnType = typeRef(3);
					}
					} 
				}
				setState(80);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TypePrimaryContext extends ParserRuleContext {
		public TypePrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typePrimary; }
	 
		public TypePrimaryContext() { }
		public void copyFrom(TypePrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PointerTypeRefContext extends TypePrimaryContext {
		public TypePrimaryContext type;
		public TerminalNode TIMES() { return getToken(PlankParser.TIMES, 0); }
		public TypePrimaryContext typePrimary() {
			return getRuleContext(TypePrimaryContext.class,0);
		}
		public PointerTypeRefContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class ArrayTypeRefContext extends TypePrimaryContext {
		public TypePrimaryContext type;
		public TerminalNode LBRACKET() { return getToken(PlankParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(PlankParser.RBRACKET, 0); }
		public TypePrimaryContext typePrimary() {
			return getRuleContext(TypePrimaryContext.class,0);
		}
		public ArrayTypeRefContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class AccessTypeRefContext extends TypePrimaryContext {
		public QualifiedPathContext path;
		public QualifiedPathContext qualifiedPath() {
			return getRuleContext(QualifiedPathContext.class,0);
		}
		public AccessTypeRefContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class GroupTypeRefContext extends TypePrimaryContext {
		public TypeRefContext type;
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public GroupTypeRefContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}

	public final TypePrimaryContext typePrimary() throws RecognitionException {
		TypePrimaryContext _localctx = new TypePrimaryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_typePrimary);
		try {
			setState(92);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				_localctx = new AccessTypeRefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				((AccessTypeRefContext)_localctx).path = qualifiedPath();
				}
				break;
			case LBRACKET:
				_localctx = new ArrayTypeRefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				match(LBRACKET);
				setState(83);
				((ArrayTypeRefContext)_localctx).type = typePrimary();
				setState(84);
				match(RBRACKET);
				}
				break;
			case TIMES:
				_localctx = new PointerTypeRefContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(86);
				match(TIMES);
				setState(87);
				((PointerTypeRefContext)_localctx).type = typePrimary();
				}
				break;
			case LPAREN:
				_localctx = new GroupTypeRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(88);
				match(LPAREN);
				setState(89);
				((GroupTypeRefContext)_localctx).type = typeRef(0);
				setState(90);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public Token name;
		public TypeRefContext type;
		public TerminalNode COLON() { return getToken(PlankParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			((ParamContext)_localctx).name = match(IDENTIFIER);
			setState(95);
			match(COLON);
			setState(96);
			((ParamContext)_localctx).type = typeRef(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclContext extends ParserRuleContext {
		public DeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decl; }
	 
		public DeclContext() { }
		public void copyFrom(DeclContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FunDeclContext extends DeclContext {
		public Token name;
		public TypeRefContext returnType;
		public TerminalNode FUN() { return getToken(PlankParser.FUN, 0); }
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public List<AttrContext> attr() {
			return getRuleContexts(AttrContext.class);
		}
		public AttrContext attr(int i) {
			return getRuleContext(AttrContext.class,i);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public TerminalNode COLON() { return getToken(PlankParser.COLON, 0); }
		public TerminalNode LBRACE() { return getToken(PlankParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(PlankParser.RBRACE, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public FunDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class LetDeclContext extends DeclContext {
		public Token name;
		public TypeRefContext type;
		public ExprContext value;
		public TerminalNode LET() { return getToken(PlankParser.LET, 0); }
		public TerminalNode COLON() { return getToken(PlankParser.COLON, 0); }
		public TerminalNode EQUAL() { return getToken(PlankParser.EQUAL, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode MUTABLE() { return getToken(PlankParser.MUTABLE, 0); }
		public LetDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class ImportDeclContext extends DeclContext {
		public QualifiedPathContext path;
		public TerminalNode IMPORT() { return getToken(PlankParser.IMPORT, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public QualifiedPathContext qualifiedPath() {
			return getRuleContext(QualifiedPathContext.class,0);
		}
		public ImportDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class ModuleDeclContext extends DeclContext {
		public QualifiedPathContext path;
		public TerminalNode MODULE() { return getToken(PlankParser.MODULE, 0); }
		public TerminalNode LBRACE() { return getToken(PlankParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(PlankParser.RBRACE, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public QualifiedPathContext qualifiedPath() {
			return getRuleContext(QualifiedPathContext.class,0);
		}
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public ModuleDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class StructDeclContext extends DeclContext {
		public Token name;
		public TerminalNode TYPE() { return getToken(PlankParser.TYPE, 0); }
		public TerminalNode EQUAL() { return getToken(PlankParser.EQUAL, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TerminalNode LBRACE() { return getToken(PlankParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(PlankParser.RBRACE, 0); }
		public List<PropContext> prop() {
			return getRuleContexts(PropContext.class);
		}
		public PropContext prop(int i) {
			return getRuleContext(PropContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public StructDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class InferLetDeclContext extends DeclContext {
		public Token name;
		public ExprContext value;
		public TerminalNode LET() { return getToken(PlankParser.LET, 0); }
		public TerminalNode EQUAL() { return getToken(PlankParser.EQUAL, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode MUTABLE() { return getToken(PlankParser.MUTABLE, 0); }
		public InferLetDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}
	public static class EnumDeclContext extends DeclContext {
		public Token name;
		public TerminalNode TYPE() { return getToken(PlankParser.TYPE, 0); }
		public TerminalNode EQUAL() { return getToken(PlankParser.EQUAL, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public List<TerminalNode> BAR() { return getTokens(PlankParser.BAR); }
		public TerminalNode BAR(int i) {
			return getToken(PlankParser.BAR, i);
		}
		public List<EnumMemberContext> enumMember() {
			return getRuleContexts(EnumMemberContext.class);
		}
		public EnumMemberContext enumMember(int i) {
			return getRuleContext(EnumMemberContext.class,i);
		}
		public EnumDeclContext(DeclContext ctx) { copyFrom(ctx); }
	}

	public final DeclContext decl() throws RecognitionException {
		DeclContext _localctx = new DeclContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_decl);
		int _la;
		try {
			setState(196);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new StructDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(TYPE);
				setState(99);
				((StructDeclContext)_localctx).name = match(IDENTIFIER);
				setState(100);
				match(EQUAL);
				{
				setState(101);
				match(LBRACE);
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUTABLE || _la==IDENTIFIER) {
					{
					setState(102);
					prop();
					setState(107);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(103);
						match(COMMA);
						setState(104);
						prop();
						}
						}
						setState(109);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(112);
				match(RBRACE);
				}
				setState(114);
				semis();
				}
				break;
			case 2:
				_localctx = new EnumDeclContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(115);
				match(TYPE);
				setState(116);
				((EnumDeclContext)_localctx).name = match(IDENTIFIER);
				setState(117);
				match(EQUAL);
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BAR) {
					{
					{
					setState(118);
					match(BAR);
					setState(119);
					enumMember();
					}
					}
					setState(124);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(125);
				semis();
				}
				break;
			case 3:
				_localctx = new ModuleDeclContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(126);
				match(MODULE);
				setState(127);
				((ModuleDeclContext)_localctx).path = qualifiedPath();
				setState(128);
				match(LBRACE);
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << FUN) | (1L << TYPE) | (1L << LET) | (1L << IMPORT) | (1L << MODULE))) != 0)) {
					{
					{
					setState(129);
					decl();
					}
					}
					setState(134);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(135);
				match(RBRACE);
				setState(136);
				semis();
				}
				break;
			case 4:
				_localctx = new ImportDeclContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(138);
				match(IMPORT);
				setState(139);
				((ImportDeclContext)_localctx).path = qualifiedPath();
				setState(140);
				semis();
				}
				break;
			case 5:
				_localctx = new FunDeclContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(142);
					attr();
					}
					}
					setState(147);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(148);
				match(FUN);
				setState(149);
				((FunDeclContext)_localctx).name = match(IDENTIFIER);
				setState(150);
				match(LPAREN);
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(151);
					param();
					setState(156);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(152);
						match(COMMA);
						setState(153);
						param();
						}
						}
						setState(158);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(161);
				match(RPAREN);
				setState(164);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(162);
					match(COLON);
					setState(163);
					((FunDeclContext)_localctx).returnType = typeRef(0);
					}
				}

				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(166);
					match(LBRACE);
					setState(170);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << LPAREN) | (1L << LBRACKET) | (1L << AMPERSTAND) | (1L << SUB) | (1L << TIMES) | (1L << BANG) | (1L << RETURN) | (1L << FUN) | (1L << TYPE) | (1L << LET) | (1L << IF) | (1L << TRUE) | (1L << FALSE) | (1L << IMPORT) | (1L << SIZEOF) | (1L << MODULE) | (1L << MATCH) | (1L << IDENTIFIER) | (1L << STRING) | (1L << INT) | (1L << DECIMAL))) != 0)) {
						{
						{
						setState(167);
						stmt();
						}
						}
						setState(172);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(173);
					match(RBRACE);
					}
				}

				}
				break;
			case 6:
				_localctx = new InferLetDeclContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(176);
				match(LET);
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUTABLE) {
					{
					setState(177);
					match(MUTABLE);
					}
				}

				setState(180);
				((InferLetDeclContext)_localctx).name = match(IDENTIFIER);
				setState(181);
				match(EQUAL);
				setState(182);
				((InferLetDeclContext)_localctx).value = expr(0);
				setState(183);
				semis();
				}
				break;
			case 7:
				_localctx = new LetDeclContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(185);
				match(LET);
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUTABLE) {
					{
					setState(186);
					match(MUTABLE);
					}
				}

				setState(189);
				((LetDeclContext)_localctx).name = match(IDENTIFIER);
				setState(190);
				match(COLON);
				setState(191);
				((LetDeclContext)_localctx).type = typeRef(0);
				setState(192);
				match(EQUAL);
				setState(193);
				((LetDeclContext)_localctx).value = expr(0);
				setState(194);
				semis();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnumMemberContext extends ParserRuleContext {
		public Token name;
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public List<TypeRefContext> typeRef() {
			return getRuleContexts(TypeRefContext.class);
		}
		public TypeRefContext typeRef(int i) {
			return getRuleContext(TypeRefContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(PlankParser.COMMA, 0); }
		public EnumMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumMember; }
	}

	public final EnumMemberContext enumMember() throws RecognitionException {
		EnumMemberContext _localctx = new EnumMemberContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_enumMember);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			((EnumMemberContext)_localctx).name = match(IDENTIFIER);
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(199);
				match(LPAREN);
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACKET) | (1L << TIMES) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(200);
					typeRef(0);
					{
					setState(201);
					match(COMMA);
					setState(202);
					typeRef(0);
					}
					}
				}

				setState(206);
				match(RPAREN);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropContext extends ParserRuleContext {
		public Token name;
		public TypeRefContext type;
		public TerminalNode COLON() { return getToken(PlankParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public TerminalNode MUTABLE() { return getToken(PlankParser.MUTABLE, 0); }
		public PropContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prop; }
	}

	public final PropContext prop() throws RecognitionException {
		PropContext _localctx = new PropContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_prop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MUTABLE) {
				{
				setState(209);
				match(MUTABLE);
				}
			}

			setState(212);
			((PropContext)_localctx).name = match(IDENTIFIER);
			setState(213);
			match(COLON);
			setState(214);
			((PropContext)_localctx).type = typeRef(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrContext extends ParserRuleContext {
		public Token name;
		public TerminalNode AT() { return getToken(PlankParser.AT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public List<AttrExprContext> attrExpr() {
			return getRuleContexts(AttrExprContext.class);
		}
		public AttrExprContext attrExpr(int i) {
			return getRuleContext(AttrExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public AttrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attr; }
	}

	public final AttrContext attr() throws RecognitionException {
		AttrContext _localctx = new AttrContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_attr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			match(AT);
			setState(217);
			((AttrContext)_localctx).name = match(IDENTIFIER);
			setState(230);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(218);
				match(LPAREN);
				setState(227);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << IDENTIFIER) | (1L << STRING) | (1L << INT) | (1L << DECIMAL))) != 0)) {
					{
					setState(219);
					attrExpr();
					setState(224);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(220);
						match(COMMA);
						setState(221);
						attrExpr();
						}
						}
						setState(226);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(229);
				match(RPAREN);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrExprContext extends ParserRuleContext {
		public AttrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrExpr; }
	 
		public AttrExprContext() { }
		public void copyFrom(AttrExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AttrIntExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode INT() { return getToken(PlankParser.INT, 0); }
		public AttrIntExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}
	public static class AttrAccessExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public AttrAccessExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}
	public static class AttrFalseExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode FALSE() { return getToken(PlankParser.FALSE, 0); }
		public AttrFalseExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}
	public static class AttrTrueExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode TRUE() { return getToken(PlankParser.TRUE, 0); }
		public AttrTrueExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}
	public static class AttrStringExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode STRING() { return getToken(PlankParser.STRING, 0); }
		public AttrStringExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}
	public static class AttrDecimalExprContext extends AttrExprContext {
		public Token value;
		public TerminalNode DECIMAL() { return getToken(PlankParser.DECIMAL, 0); }
		public AttrDecimalExprContext(AttrExprContext ctx) { copyFrom(ctx); }
	}

	public final AttrExprContext attrExpr() throws RecognitionException {
		AttrExprContext _localctx = new AttrExprContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_attrExpr);
		try {
			setState(238);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				_localctx = new AttrIntExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(232);
				((AttrIntExprContext)_localctx).value = match(INT);
				}
				break;
			case DECIMAL:
				_localctx = new AttrDecimalExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(233);
				((AttrDecimalExprContext)_localctx).value = match(DECIMAL);
				}
				break;
			case STRING:
				_localctx = new AttrStringExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(234);
				((AttrStringExprContext)_localctx).value = match(STRING);
				}
				break;
			case IDENTIFIER:
				_localctx = new AttrAccessExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(235);
				((AttrAccessExprContext)_localctx).value = match(IDENTIFIER);
				}
				break;
			case TRUE:
				_localctx = new AttrTrueExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(236);
				((AttrTrueExprContext)_localctx).value = match(TRUE);
				}
				break;
			case FALSE:
				_localctx = new AttrFalseExprContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(237);
				((AttrFalseExprContext)_localctx).value = match(FALSE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprStmtContext extends StmtContext {
		public ExprContext value;
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprStmtContext(StmtContext ctx) { copyFrom(ctx); }
	}
	public static class DeclStmtContext extends StmtContext {
		public DeclContext value;
		public DeclContext decl() {
			return getRuleContext(DeclContext.class,0);
		}
		public DeclStmtContext(StmtContext ctx) { copyFrom(ctx); }
	}
	public static class ReturnStmtContext extends StmtContext {
		public ExprContext value;
		public TerminalNode RETURN() { return getToken(PlankParser.RETURN, 0); }
		public SemisContext semis() {
			return getRuleContext(SemisContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnStmtContext(StmtContext ctx) { copyFrom(ctx); }
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_stmt);
		int _la;
		try {
			setState(249);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
			case FUN:
			case TYPE:
			case LET:
			case IMPORT:
			case MODULE:
				_localctx = new DeclStmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(240);
				((DeclStmtContext)_localctx).value = decl();
				}
				break;
			case LPAREN:
			case LBRACKET:
			case AMPERSTAND:
			case SUB:
			case TIMES:
			case BANG:
			case IF:
			case TRUE:
			case FALSE:
			case SIZEOF:
			case MATCH:
			case IDENTIFIER:
			case STRING:
			case INT:
			case DECIMAL:
				_localctx = new ExprStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(241);
				((ExprStmtContext)_localctx).value = expr(0);
				setState(242);
				semis();
				}
				break;
			case RETURN:
				_localctx = new ReturnStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(244);
				match(RETURN);
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACKET) | (1L << AMPERSTAND) | (1L << SUB) | (1L << TIMES) | (1L << BANG) | (1L << IF) | (1L << TRUE) | (1L << FALSE) | (1L << SIZEOF) | (1L << MATCH) | (1L << IDENTIFIER) | (1L << STRING) | (1L << INT) | (1L << DECIMAL))) != 0)) {
					{
					setState(245);
					((ReturnStmtContext)_localctx).value = expr(0);
					}
				}

				setState(248);
				semis();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PatternContext extends ParserRuleContext {
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
	 
		public PatternContext() { }
		public void copyFrom(PatternContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IdentPatternContext extends PatternContext {
		public Token name;
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public IdentPatternContext(PatternContext ctx) { copyFrom(ctx); }
	}
	public static class NamedTuplePatternContext extends PatternContext {
		public QualifiedPathContext type;
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public QualifiedPathContext qualifiedPath() {
			return getRuleContext(QualifiedPathContext.class,0);
		}
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public NamedTuplePatternContext(PatternContext ctx) { copyFrom(ctx); }
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pattern);
		int _la;
		try {
			setState(266);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				_localctx = new NamedTuplePatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(251);
				((NamedTuplePatternContext)_localctx).type = qualifiedPath();
				setState(252);
				match(LPAREN);
				setState(261);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(253);
					pattern();
					setState(258);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(254);
						match(COMMA);
						setState(255);
						pattern();
						}
						}
						setState(260);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(263);
				match(RPAREN);
				}
				break;
			case 2:
				_localctx = new IdentPatternContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(265);
				((IdentPatternContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IfExprContext extends ExprContext {
		public ExprContext cond;
		public ExprContext thenBranch;
		public ExprContext elseBranch;
		public TerminalNode IF() { return getToken(PlankParser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(PlankParser.ELSE, 0); }
		public IfExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class SizeofExprContext extends ExprContext {
		public TypeRefContext type;
		public TerminalNode SIZEOF() { return getToken(PlankParser.SIZEOF, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public SizeofExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class SetExprContext extends ExprContext {
		public PrimaryContext receiver;
		public ExprContext value;
		public TerminalNode ASSIGN() { return getToken(PlankParser.ASSIGN, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public SetExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class InstanceExprContext extends ExprContext {
		public TypeRefContext type;
		public TerminalNode LBRACE() { return getToken(PlankParser.LBRACE, 0); }
		public List<InstanceArgContext> instanceArg() {
			return getRuleContexts(InstanceArgContext.class);
		}
		public InstanceArgContext instanceArg(int i) {
			return getRuleContext(InstanceArgContext.class,i);
		}
		public TerminalNode RBRACE() { return getToken(PlankParser.RBRACE, 0); }
		public TypeRefContext typeRef() {
			return getRuleContext(TypeRefContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public InstanceExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class BinaryExprContext extends ExprContext {
		public ExprContext lhs;
		public Token op;
		public ExprContext rhs;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode EQ() { return getToken(PlankParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(PlankParser.NEQ, 0); }
		public TerminalNode GT() { return getToken(PlankParser.GT, 0); }
		public TerminalNode GTE() { return getToken(PlankParser.GTE, 0); }
		public TerminalNode LT() { return getToken(PlankParser.LT, 0); }
		public TerminalNode LTE() { return getToken(PlankParser.LTE, 0); }
		public TerminalNode TIMES() { return getToken(PlankParser.TIMES, 0); }
		public TerminalNode DIV() { return getToken(PlankParser.DIV, 0); }
		public TerminalNode ADD() { return getToken(PlankParser.ADD, 0); }
		public TerminalNode CONCAT() { return getToken(PlankParser.CONCAT, 0); }
		public TerminalNode SUB() { return getToken(PlankParser.SUB, 0); }
		public BinaryExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class CallExprContext extends ExprContext {
		public PrimaryContext callee;
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public CallExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class UnaryExprContext extends ExprContext {
		public Token op;
		public ExprContext rhs;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode BANG() { return getToken(PlankParser.BANG, 0); }
		public TerminalNode SUB() { return getToken(PlankParser.SUB, 0); }
		public UnaryExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class MatchExprContext extends ExprContext {
		public ExprContext subject;
		public TerminalNode MATCH() { return getToken(PlankParser.MATCH, 0); }
		public TerminalNode LBRACE() { return getToken(PlankParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(PlankParser.RBRACE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<MatchPatternContext> matchPattern() {
			return getRuleContexts(MatchPatternContext.class);
		}
		public MatchPatternContext matchPattern(int i) {
			return getRuleContext(MatchPatternContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(PlankParser.COMMA, 0); }
		public MatchExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class AssignExprContext extends ExprContext {
		public Token name;
		public ExprContext value;
		public TerminalNode ASSIGN() { return getToken(PlankParser.ASSIGN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssignExprContext(ExprContext ctx) { copyFrom(ctx); }
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				_localctx = new AssignExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(269);
				((AssignExprContext)_localctx).name = match(IDENTIFIER);
				setState(270);
				match(ASSIGN);
				setState(271);
				((AssignExprContext)_localctx).value = expr(12);
				}
				break;
			case 2:
				{
				_localctx = new SetExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(272);
				((SetExprContext)_localctx).receiver = primary();
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LPAREN || _la==DOT) {
					{
					{
					setState(273);
					arg();
					}
					}
					setState(278);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(279);
				match(ASSIGN);
				setState(280);
				((SetExprContext)_localctx).value = expr(11);
				}
				break;
			case 3:
				{
				_localctx = new UnaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(282);
				((UnaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==SUB || _la==BANG) ) {
					((UnaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(283);
				((UnaryExprContext)_localctx).rhs = expr(6);
				}
				break;
			case 4:
				{
				_localctx = new CallExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(284);
				((CallExprContext)_localctx).callee = primary();
				setState(288);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(285);
						arg();
						}
						} 
					}
					setState(290);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				}
				}
				break;
			case 5:
				{
				_localctx = new InstanceExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(291);
				((InstanceExprContext)_localctx).type = typeRef(0);
				setState(292);
				match(LBRACE);
				setState(293);
				instanceArg();
				setState(298);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(294);
					match(COMMA);
					setState(295);
					instanceArg();
					}
					}
					setState(300);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(301);
				match(RBRACE);
				}
				break;
			case 6:
				{
				_localctx = new IfExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(303);
				match(IF);
				setState(304);
				match(LPAREN);
				setState(305);
				((IfExprContext)_localctx).cond = expr(0);
				setState(306);
				match(RPAREN);
				setState(307);
				((IfExprContext)_localctx).thenBranch = expr(0);
				setState(310);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
				case 1:
					{
					setState(308);
					match(ELSE);
					setState(309);
					((IfExprContext)_localctx).elseBranch = expr(0);
					}
					break;
				}
				}
				break;
			case 7:
				{
				_localctx = new SizeofExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(312);
				match(SIZEOF);
				setState(313);
				((SizeofExprContext)_localctx).type = typeRef(0);
				}
				break;
			case 8:
				{
				_localctx = new MatchExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(314);
				match(MATCH);
				setState(315);
				((MatchExprContext)_localctx).subject = expr(0);
				setState(316);
				match(LBRACE);
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(317);
					matchPattern();
					{
					setState(318);
					match(COMMA);
					setState(319);
					matchPattern();
					}
					}
				}

				setState(323);
				match(RBRACE);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(341);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(339);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						((BinaryExprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(327);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(328);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQ || _la==NEQ) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(329);
						((BinaryExprContext)_localctx).rhs = expr(11);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						((BinaryExprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(330);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(331);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << GTE) | (1L << LTE))) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(332);
						((BinaryExprContext)_localctx).rhs = expr(10);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						((BinaryExprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(333);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(334);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==DIV || _la==TIMES) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(335);
						((BinaryExprContext)_localctx).rhs = expr(9);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						((BinaryExprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(336);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(337);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ADD) | (1L << SUB) | (1L << CONCAT))) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(338);
						((BinaryExprContext)_localctx).rhs = expr(8);
						}
						break;
					}
					} 
				}
				setState(343);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class InstanceArgContext extends ParserRuleContext {
		public Token name;
		public ExprContext value;
		public TerminalNode COLON() { return getToken(PlankParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public InstanceArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instanceArg; }
	}

	public final InstanceArgContext instanceArg() throws RecognitionException {
		InstanceArgContext _localctx = new InstanceArgContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_instanceArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			((InstanceArgContext)_localctx).name = match(IDENTIFIER);
			setState(345);
			match(COLON);
			setState(346);
			((InstanceArgContext)_localctx).value = expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgContext extends ParserRuleContext {
		public ArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg; }
	 
		public ArgContext() { }
		public void copyFrom(ArgContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CallArgContext extends ArgContext {
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlankParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlankParser.COMMA, i);
		}
		public CallArgContext(ArgContext ctx) { copyFrom(ctx); }
	}
	public static class GetArgContext extends ArgContext {
		public Token name;
		public TerminalNode DOT() { return getToken(PlankParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public GetArgContext(ArgContext ctx) { copyFrom(ctx); }
	}

	public final ArgContext arg() throws RecognitionException {
		ArgContext _localctx = new ArgContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_arg);
		int _la;
		try {
			setState(362);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				_localctx = new CallArgContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(348);
				match(LPAREN);
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACKET) | (1L << AMPERSTAND) | (1L << SUB) | (1L << TIMES) | (1L << BANG) | (1L << IF) | (1L << TRUE) | (1L << FALSE) | (1L << SIZEOF) | (1L << MATCH) | (1L << IDENTIFIER) | (1L << STRING) | (1L << INT) | (1L << DECIMAL))) != 0)) {
					{
					setState(349);
					expr(0);
					setState(354);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(350);
						match(COMMA);
						setState(351);
						expr(0);
						}
						}
						setState(356);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(359);
				match(RPAREN);
				}
				break;
			case DOT:
				_localctx = new GetArgContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(360);
				match(DOT);
				setState(361);
				((GetArgContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatchPatternContext extends ParserRuleContext {
		public PatternContext key;
		public ExprContext value;
		public TerminalNode DOUBLE_ARROW_LEFT() { return getToken(PlankParser.DOUBLE_ARROW_LEFT, 0); }
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MatchPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchPattern; }
	}

	public final MatchPatternContext matchPattern() throws RecognitionException {
		MatchPatternContext _localctx = new MatchPatternContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_matchPattern);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			((MatchPatternContext)_localctx).key = pattern();
			setState(365);
			match(DOUBLE_ARROW_LEFT);
			setState(366);
			((MatchPatternContext)_localctx).value = expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
	 
		public PrimaryContext() { }
		public void copyFrom(PrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AccessExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode IDENTIFIER() { return getToken(PlankParser.IDENTIFIER, 0); }
		public AccessExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class StringExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode STRING() { return getToken(PlankParser.STRING, 0); }
		public StringExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class GroupExprContext extends PrimaryContext {
		public ExprContext value;
		public TerminalNode LPAREN() { return getToken(PlankParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlankParser.RPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public GroupExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class TrueExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode TRUE() { return getToken(PlankParser.TRUE, 0); }
		public TrueExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class DerefExprContext extends PrimaryContext {
		public ExprContext value;
		public TerminalNode TIMES() { return getToken(PlankParser.TIMES, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DerefExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class IntExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode INT() { return getToken(PlankParser.INT, 0); }
		public IntExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class DecimalExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode DECIMAL() { return getToken(PlankParser.DECIMAL, 0); }
		public DecimalExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class RefExprContext extends PrimaryContext {
		public ExprContext value;
		public TerminalNode AMPERSTAND() { return getToken(PlankParser.AMPERSTAND, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RefExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class FalseExprContext extends PrimaryContext {
		public Token value;
		public TerminalNode FALSE() { return getToken(PlankParser.FALSE, 0); }
		public FalseExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_primary);
		try {
			setState(382);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AMPERSTAND:
				_localctx = new RefExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(368);
				match(AMPERSTAND);
				setState(369);
				((RefExprContext)_localctx).value = expr(0);
				}
				break;
			case TIMES:
				_localctx = new DerefExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(370);
				match(TIMES);
				setState(371);
				((DerefExprContext)_localctx).value = expr(0);
				}
				break;
			case INT:
				_localctx = new IntExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(372);
				((IntExprContext)_localctx).value = match(INT);
				}
				break;
			case DECIMAL:
				_localctx = new DecimalExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(373);
				((DecimalExprContext)_localctx).value = match(DECIMAL);
				}
				break;
			case STRING:
				_localctx = new StringExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(374);
				((StringExprContext)_localctx).value = match(STRING);
				}
				break;
			case IDENTIFIER:
				_localctx = new AccessExprContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(375);
				((AccessExprContext)_localctx).value = match(IDENTIFIER);
				}
				break;
			case TRUE:
				_localctx = new TrueExprContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(376);
				((TrueExprContext)_localctx).value = match(TRUE);
				}
				break;
			case FALSE:
				_localctx = new FalseExprContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(377);
				((FalseExprContext)_localctx).value = match(FALSE);
				}
				break;
			case LPAREN:
				_localctx = new GroupExprContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(378);
				match(LPAREN);
				setState(379);
				((GroupExprContext)_localctx).value = expr(0);
				setState(380);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return typeRef_sempred((TypeRefContext)_localctx, predIndex);
		case 15:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean typeRef_sempred(TypeRefContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 10);
		case 2:
			return precpred(_ctx, 9);
		case 3:
			return precpred(_ctx, 8);
		case 4:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u0183\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\5\2,\n\2\3\2\7\2/\n\2\f\2\16\2\62\13"+
		"\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\7\5<\n\5\f\5\16\5?\13\5\3\6\3\6\3\6"+
		"\7\6D\n\6\f\6\16\6G\13\6\3\7\3\7\3\7\3\7\3\7\3\7\7\7O\n\7\f\7\16\7R\13"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b_\n\b\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\nl\n\n\f\n\16\no\13\n\5\nq\n\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n{\n\n\f\n\16\n~\13\n\3\n\3\n\3\n\3\n\3\n"+
		"\7\n\u0085\n\n\f\n\16\n\u0088\13\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n"+
		"\u0092\n\n\f\n\16\n\u0095\13\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u009d\n\n\f"+
		"\n\16\n\u00a0\13\n\5\n\u00a2\n\n\3\n\3\n\3\n\5\n\u00a7\n\n\3\n\3\n\7\n"+
		"\u00ab\n\n\f\n\16\n\u00ae\13\n\3\n\5\n\u00b1\n\n\3\n\3\n\5\n\u00b5\n\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00be\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\5\n\u00c7\n\n\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00cf\n\13\3\13\5\13"+
		"\u00d2\n\13\3\f\5\f\u00d5\n\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\7\r\u00e1\n\r\f\r\16\r\u00e4\13\r\5\r\u00e6\n\r\3\r\5\r\u00e9\n\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\5\16\u00f1\n\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\5\17\u00f9\n\17\3\17\5\17\u00fc\n\17\3\20\3\20\3\20\3\20\3\20\7\20\u0103"+
		"\n\20\f\20\16\20\u0106\13\20\5\20\u0108\n\20\3\20\3\20\3\20\5\20\u010d"+
		"\n\20\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u0115\n\21\f\21\16\21\u0118\13"+
		"\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u0121\n\21\f\21\16\21\u0124"+
		"\13\21\3\21\3\21\3\21\3\21\3\21\7\21\u012b\n\21\f\21\16\21\u012e\13\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0139\n\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0144\n\21\3\21\3\21\5\21\u0148"+
		"\n\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\7\21"+
		"\u0156\n\21\f\21\16\21\u0159\13\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23"+
		"\3\23\7\23\u0163\n\23\f\23\16\23\u0166\13\23\5\23\u0168\n\23\3\23\3\23"+
		"\3\23\5\23\u016d\n\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0181\n\25\3\25\2\4\f \26"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(\2\b\4\2\3\3\6\6\4\2\24\24"+
		"\30\30\3\2\37 \3\2\33\36\3\2\25\26\4\2\23\24\27\27\2\u01b4\2+\3\2\2\2"+
		"\4\63\3\2\2\2\6\67\3\2\2\2\b=\3\2\2\2\n@\3\2\2\2\fH\3\2\2\2\16^\3\2\2"+
		"\2\20`\3\2\2\2\22\u00c6\3\2\2\2\24\u00c8\3\2\2\2\26\u00d4\3\2\2\2\30\u00da"+
		"\3\2\2\2\32\u00f0\3\2\2\2\34\u00fb\3\2\2\2\36\u010c\3\2\2\2 \u0147\3\2"+
		"\2\2\"\u015a\3\2\2\2$\u016c\3\2\2\2&\u016e\3\2\2\2(\u0180\3\2\2\2*,\5"+
		"\4\3\2+*\3\2\2\2+,\3\2\2\2,\60\3\2\2\2-/\5\22\n\2.-\3\2\2\2/\62\3\2\2"+
		"\2\60.\3\2\2\2\60\61\3\2\2\2\61\3\3\2\2\2\62\60\3\2\2\2\63\64\7.\2\2\64"+
		"\65\5\n\6\2\65\66\5\6\4\2\66\5\3\2\2\2\678\7\6\2\289\5\b\5\29\7\3\2\2"+
		"\2:<\t\2\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>\t\3\2\2\2?=\3\2"+
		"\2\2@E\7\61\2\2AB\7\21\2\2BD\7\61\2\2CA\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF"+
		"\3\2\2\2F\13\3\2\2\2GE\3\2\2\2HI\b\7\1\2IJ\5\16\b\2JP\3\2\2\2KL\f\4\2"+
		"\2LM\7\"\2\2MO\5\f\7\5NK\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\r\3\2"+
		"\2\2RP\3\2\2\2S_\5\n\6\2TU\7\16\2\2UV\5\16\b\2VW\7\17\2\2W_\3\2\2\2XY"+
		"\7\26\2\2Y_\5\16\b\2Z[\7\n\2\2[\\\5\f\7\2\\]\7\13\2\2]_\3\2\2\2^S\3\2"+
		"\2\2^T\3\2\2\2^X\3\2\2\2^Z\3\2\2\2_\17\3\2\2\2`a\7\61\2\2ab\7\b\2\2bc"+
		"\5\f\7\2c\21\3\2\2\2de\7%\2\2ef\7\61\2\2fg\7\31\2\2gp\7\f\2\2hm\5\26\f"+
		"\2ij\7\7\2\2jl\5\26\f\2ki\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2nq\3\2"+
		"\2\2om\3\2\2\2ph\3\2\2\2pq\3\2\2\2qr\3\2\2\2rs\7\r\2\2st\3\2\2\2t\u00c7"+
		"\5\6\4\2uv\7%\2\2vw\7\61\2\2w|\7\31\2\2xy\7\t\2\2y{\5\24\13\2zx\3\2\2"+
		"\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u00c7\5\6"+
		"\4\2\u0080\u0081\7.\2\2\u0081\u0082\5\n\6\2\u0082\u0086\7\f\2\2\u0083"+
		"\u0085\5\22\n\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3"+
		"\2\2\2\u0086\u0087\3\2\2\2\u0087\u0089\3\2\2\2\u0088\u0086\3\2\2\2\u0089"+
		"\u008a\7\r\2\2\u008a\u008b\5\6\4\2\u008b\u00c7\3\2\2\2\u008c\u008d\7,"+
		"\2\2\u008d\u008e\5\n\6\2\u008e\u008f\5\6\4\2\u008f\u00c7\3\2\2\2\u0090"+
		"\u0092\5\30\r\2\u0091\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3"+
		"\2\2\2\u0093\u0094\3\2\2\2\u0094\u0096\3\2\2\2\u0095\u0093\3\2\2\2\u0096"+
		"\u0097\7$\2\2\u0097\u0098\7\61\2\2\u0098\u00a1\7\n\2\2\u0099\u009e\5\20"+
		"\t\2\u009a\u009b\7\7\2\2\u009b\u009d\5\20\t\2\u009c\u009a\3\2\2\2\u009d"+
		"\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a2\3\2"+
		"\2\2\u00a0\u009e\3\2\2\2\u00a1\u0099\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\u00a3\3\2\2\2\u00a3\u00a6\7\13\2\2\u00a4\u00a5\7\b\2\2\u00a5\u00a7\5"+
		"\f\7\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00b0\3\2\2\2\u00a8"+
		"\u00ac\7\f\2\2\u00a9\u00ab\5\34\17\2\u00aa\u00a9\3\2\2\2\u00ab\u00ae\3"+
		"\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae"+
		"\u00ac\3\2\2\2\u00af\u00b1\7\r\2\2\u00b0\u00a8\3\2\2\2\u00b0\u00b1\3\2"+
		"\2\2\u00b1\u00c7\3\2\2\2\u00b2\u00b4\7&\2\2\u00b3\u00b5\7)\2\2\u00b4\u00b3"+
		"\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\7\61\2\2"+
		"\u00b7\u00b8\7\31\2\2\u00b8\u00b9\5 \21\2\u00b9\u00ba\5\6\4\2\u00ba\u00c7"+
		"\3\2\2\2\u00bb\u00bd\7&\2\2\u00bc\u00be\7)\2\2\u00bd\u00bc\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\7\61\2\2\u00c0\u00c1\7"+
		"\b\2\2\u00c1\u00c2\5\f\7\2\u00c2\u00c3\7\31\2\2\u00c3\u00c4\5 \21\2\u00c4"+
		"\u00c5\5\6\4\2\u00c5\u00c7\3\2\2\2\u00c6d\3\2\2\2\u00c6u\3\2\2\2\u00c6"+
		"\u0080\3\2\2\2\u00c6\u008c\3\2\2\2\u00c6\u0093\3\2\2\2\u00c6\u00b2\3\2"+
		"\2\2\u00c6\u00bb\3\2\2\2\u00c7\23\3\2\2\2\u00c8\u00d1\7\61\2\2\u00c9\u00ce"+
		"\7\n\2\2\u00ca\u00cb\5\f\7\2\u00cb\u00cc\7\7\2\2\u00cc\u00cd\5\f\7\2\u00cd"+
		"\u00cf\3\2\2\2\u00ce\u00ca\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\u00d2\7\13\2\2\u00d1\u00c9\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2"+
		"\25\3\2\2\2\u00d3\u00d5\7)\2\2\u00d4\u00d3\3\2\2\2\u00d4\u00d5\3\2\2\2"+
		"\u00d5\u00d6\3\2\2\2\u00d6\u00d7\7\61\2\2\u00d7\u00d8\7\b\2\2\u00d8\u00d9"+
		"\5\f\7\2\u00d9\27\3\2\2\2\u00da\u00db\7\5\2\2\u00db\u00e8\7\61\2\2\u00dc"+
		"\u00e5\7\n\2\2\u00dd\u00e2\5\32\16\2\u00de\u00df\7\7\2\2\u00df\u00e1\5"+
		"\32\16\2\u00e0\u00de\3\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2"+
		"\u00e3\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00dd\3\2"+
		"\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e9\7\13\2\2\u00e8"+
		"\u00dc\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\31\3\2\2\2\u00ea\u00f1\7\63\2"+
		"\2\u00eb\u00f1\7\64\2\2\u00ec\u00f1\7\62\2\2\u00ed\u00f1\7\61\2\2\u00ee"+
		"\u00f1\7*\2\2\u00ef\u00f1\7+\2\2\u00f0\u00ea\3\2\2\2\u00f0\u00eb\3\2\2"+
		"\2\u00f0\u00ec\3\2\2\2\u00f0\u00ed\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f0\u00ef"+
		"\3\2\2\2\u00f1\33\3\2\2\2\u00f2\u00fc\5\22\n\2\u00f3\u00f4\5 \21\2\u00f4"+
		"\u00f5\5\6\4\2\u00f5\u00fc\3\2\2\2\u00f6\u00f8\7#\2\2\u00f7\u00f9\5 \21"+
		"\2\u00f8\u00f7\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fc"+
		"\5\6\4\2\u00fb\u00f2\3\2\2\2\u00fb\u00f3\3\2\2\2\u00fb\u00f6\3\2\2\2\u00fc"+
		"\35\3\2\2\2\u00fd\u00fe\5\n\6\2\u00fe\u0107\7\n\2\2\u00ff\u0104\5\36\20"+
		"\2\u0100\u0101\7\7\2\2\u0101\u0103\5\36\20\2\u0102\u0100\3\2\2\2\u0103"+
		"\u0106\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0108\3\2"+
		"\2\2\u0106\u0104\3\2\2\2\u0107\u00ff\3\2\2\2\u0107\u0108\3\2\2\2\u0108"+
		"\u0109\3\2\2\2\u0109\u010a\7\13\2\2\u010a\u010d\3\2\2\2\u010b\u010d\7"+
		"\61\2\2\u010c\u00fd\3\2\2\2\u010c\u010b\3\2\2\2\u010d\37\3\2\2\2\u010e"+
		"\u010f\b\21\1\2\u010f\u0110\7\61\2\2\u0110\u0111\7\32\2\2\u0111\u0148"+
		"\5 \21\16\u0112\u0116\5(\25\2\u0113\u0115\5$\23\2\u0114\u0113\3\2\2\2"+
		"\u0115\u0118\3\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0119"+
		"\3\2\2\2\u0118\u0116\3\2\2\2\u0119\u011a\7\32\2\2\u011a\u011b\5 \21\r"+
		"\u011b\u0148\3\2\2\2\u011c\u011d\t\3\2\2\u011d\u0148\5 \21\b\u011e\u0122"+
		"\5(\25\2\u011f\u0121\5$\23\2\u0120\u011f\3\2\2\2\u0121\u0124\3\2\2\2\u0122"+
		"\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0148\3\2\2\2\u0124\u0122\3\2"+
		"\2\2\u0125\u0126\5\f\7\2\u0126\u0127\7\f\2\2\u0127\u012c\5\"\22\2\u0128"+
		"\u0129\7\7\2\2\u0129\u012b\5\"\22\2\u012a\u0128\3\2\2\2\u012b\u012e\3"+
		"\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012f\3\2\2\2\u012e"+
		"\u012c\3\2\2\2\u012f\u0130\7\r\2\2\u0130\u0148\3\2\2\2\u0131\u0132\7\'"+
		"\2\2\u0132\u0133\7\n\2\2\u0133\u0134\5 \21\2\u0134\u0135\7\13\2\2\u0135"+
		"\u0138\5 \21\2\u0136\u0137\7(\2\2\u0137\u0139\5 \21\2\u0138\u0136\3\2"+
		"\2\2\u0138\u0139\3\2\2\2\u0139\u0148\3\2\2\2\u013a\u013b\7-\2\2\u013b"+
		"\u0148\5\f\7\2\u013c\u013d\7/\2\2\u013d\u013e\5 \21\2\u013e\u0143\7\f"+
		"\2\2\u013f\u0140\5&\24\2\u0140\u0141\7\7\2\2\u0141\u0142\5&\24\2\u0142"+
		"\u0144\3\2\2\2\u0143\u013f\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\3\2"+
		"\2\2\u0145\u0146\7\r\2\2\u0146\u0148\3\2\2\2\u0147\u010e\3\2\2\2\u0147"+
		"\u0112\3\2\2\2\u0147\u011c\3\2\2\2\u0147\u011e\3\2\2\2\u0147\u0125\3\2"+
		"\2\2\u0147\u0131\3\2\2\2\u0147\u013a\3\2\2\2\u0147\u013c\3\2\2\2\u0148"+
		"\u0157\3\2\2\2\u0149\u014a\f\f\2\2\u014a\u014b\t\4\2\2\u014b\u0156\5 "+
		"\21\r\u014c\u014d\f\13\2\2\u014d\u014e\t\5\2\2\u014e\u0156\5 \21\f\u014f"+
		"\u0150\f\n\2\2\u0150\u0151\t\6\2\2\u0151\u0156\5 \21\13\u0152\u0153\f"+
		"\t\2\2\u0153\u0154\t\7\2\2\u0154\u0156\5 \21\n\u0155\u0149\3\2\2\2\u0155"+
		"\u014c\3\2\2\2\u0155\u014f\3\2\2\2\u0155\u0152\3\2\2\2\u0156\u0159\3\2"+
		"\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158!\3\2\2\2\u0159\u0157"+
		"\3\2\2\2\u015a\u015b\7\61\2\2\u015b\u015c\7\b\2\2\u015c\u015d\5 \21\2"+
		"\u015d#\3\2\2\2\u015e\u0167\7\n\2\2\u015f\u0164\5 \21\2\u0160\u0161\7"+
		"\7\2\2\u0161\u0163\5 \21\2\u0162\u0160\3\2\2\2\u0163\u0166\3\2\2\2\u0164"+
		"\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0168\3\2\2\2\u0166\u0164\3\2"+
		"\2\2\u0167\u015f\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0169\3\2\2\2\u0169"+
		"\u016d\7\13\2\2\u016a\u016b\7\21\2\2\u016b\u016d\7\61\2\2\u016c\u015e"+
		"\3\2\2\2\u016c\u016a\3\2\2\2\u016d%\3\2\2\2\u016e\u016f\5\36\20\2\u016f"+
		"\u0170\7!\2\2\u0170\u0171\5 \21\2\u0171\'\3\2\2\2\u0172\u0173\7\22\2\2"+
		"\u0173\u0181\5 \21\2\u0174\u0175\7\26\2\2\u0175\u0181\5 \21\2\u0176\u0181"+
		"\7\63\2\2\u0177\u0181\7\64\2\2\u0178\u0181\7\62\2\2\u0179\u0181\7\61\2"+
		"\2\u017a\u0181\7*\2\2\u017b\u0181\7+\2\2\u017c\u017d\7\n\2\2\u017d\u017e"+
		"\5 \21\2\u017e\u017f\7\13\2\2\u017f\u0181\3\2\2\2\u0180\u0172\3\2\2\2"+
		"\u0180\u0174\3\2\2\2\u0180\u0176\3\2\2\2\u0180\u0177\3\2\2\2\u0180\u0178"+
		"\3\2\2\2\u0180\u0179\3\2\2\2\u0180\u017a\3\2\2\2\u0180\u017b\3\2\2\2\u0180"+
		"\u017c\3\2\2\2\u0181)\3\2\2\2-+\60=EP^mp|\u0086\u0093\u009e\u00a1\u00a6"+
		"\u00ac\u00b0\u00b4\u00bd\u00c6\u00ce\u00d1\u00d4\u00e2\u00e5\u00e8\u00f0"+
		"\u00f8\u00fb\u0104\u0107\u010c\u0116\u0122\u012c\u0138\u0143\u0147\u0155"+
		"\u0157\u0164\u0167\u016c\u0180";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}