// Generated from /home/gabi/Projects/kotlin/jplank/parser/src/commonAntlr/antlr/PlankLexer.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PlankLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WS", "NEWLINE", "AT", "SEMICOLON", "COMMA", "COLON", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "APOSTROPHE", "DOT", 
			"AMPERSTAND", "ADD", "SUB", "DIV", "TIMES", "CONCAT", "BANG", "EQUAL", 
			"ASSIGN", "GT", "LT", "GTE", "LTE", "EQ", "NEQ", "DOUBLE_ARROW_LEFT", 
			"ARROW_LEFT", "RETURN", "FUN", "TYPE", "LET", "IF", "ELSE", "MUTABLE", 
			"TRUE", "FALSE", "IMPORT", "SIZEOF", "MODULE", "MATCH", "CASE", "IDENTIFIER", 
			"STRING", "INT", "DECIMAL"
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


	public PlankLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PlankLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u012d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2"+
		"\3\2\6\2j\n\2\r\2\16\2k\3\2\3\2\3\3\6\3q\n\3\r\3\16\3r\3\4\3\4\3\5\3\5"+
		"\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\33"+
		"\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3"+
		" \3 \3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\3$\3$\3"+
		"%\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3(\3)\3)"+
		"\3)\3)\3)\3*\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,"+
		"\3-\3-\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3\60\3\60\7\60"+
		"\u010a\n\60\f\60\16\60\u010d\13\60\3\61\3\61\3\61\3\61\7\61\u0113\n\61"+
		"\f\61\16\61\u0116\13\61\3\61\3\61\3\61\3\61\3\61\7\61\u011d\n\61\f\61"+
		"\16\61\u0120\13\61\3\61\5\61\u0123\n\61\3\62\6\62\u0126\n\62\r\62\16\62"+
		"\u0127\3\63\3\63\3\63\3\63\2\2\64\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60"+
		"_\61a\62c\63e\64\3\2\b\4\2\13\13\"\"\4\2\f\f\17\17\5\2C\\aac|\6\2\62;"+
		"C\\aac|\6\2\f\f\17\17$$^^\3\2\62;\2\u0136\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2"+
		"\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M"+
		"\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2"+
		"\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2"+
		"\3i\3\2\2\2\5p\3\2\2\2\7t\3\2\2\2\tv\3\2\2\2\13x\3\2\2\2\rz\3\2\2\2\17"+
		"|\3\2\2\2\21~\3\2\2\2\23\u0080\3\2\2\2\25\u0082\3\2\2\2\27\u0084\3\2\2"+
		"\2\31\u0086\3\2\2\2\33\u0088\3\2\2\2\35\u008a\3\2\2\2\37\u008c\3\2\2\2"+
		"!\u008e\3\2\2\2#\u0090\3\2\2\2%\u0092\3\2\2\2\'\u0094\3\2\2\2)\u0096\3"+
		"\2\2\2+\u0098\3\2\2\2-\u009b\3\2\2\2/\u009d\3\2\2\2\61\u009f\3\2\2\2\63"+
		"\u00a2\3\2\2\2\65\u00a4\3\2\2\2\67\u00a6\3\2\2\29\u00a9\3\2\2\2;\u00ac"+
		"\3\2\2\2=\u00af\3\2\2\2?\u00b2\3\2\2\2A\u00b5\3\2\2\2C\u00b8\3\2\2\2E"+
		"\u00bf\3\2\2\2G\u00c3\3\2\2\2I\u00c8\3\2\2\2K\u00cc\3\2\2\2M\u00cf\3\2"+
		"\2\2O\u00d4\3\2\2\2Q\u00dc\3\2\2\2S\u00e1\3\2\2\2U\u00e7\3\2\2\2W\u00ee"+
		"\3\2\2\2Y\u00f5\3\2\2\2[\u00fc\3\2\2\2]\u0102\3\2\2\2_\u0107\3\2\2\2a"+
		"\u0122\3\2\2\2c\u0125\3\2\2\2e\u0129\3\2\2\2gj\t\2\2\2hj\5\5\3\2ig\3\2"+
		"\2\2ih\3\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2lm\3\2\2\2mn\b\2\2\2n\4\3"+
		"\2\2\2oq\t\3\2\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2s\6\3\2\2\2tu"+
		"\7B\2\2u\b\3\2\2\2vw\7=\2\2w\n\3\2\2\2xy\7.\2\2y\f\3\2\2\2z{\7<\2\2{\16"+
		"\3\2\2\2|}\7~\2\2}\20\3\2\2\2~\177\7*\2\2\177\22\3\2\2\2\u0080\u0081\7"+
		"+\2\2\u0081\24\3\2\2\2\u0082\u0083\7}\2\2\u0083\26\3\2\2\2\u0084\u0085"+
		"\7\177\2\2\u0085\30\3\2\2\2\u0086\u0087\7]\2\2\u0087\32\3\2\2\2\u0088"+
		"\u0089\7_\2\2\u0089\34\3\2\2\2\u008a\u008b\7)\2\2\u008b\36\3\2\2\2\u008c"+
		"\u008d\7\60\2\2\u008d \3\2\2\2\u008e\u008f\7(\2\2\u008f\"\3\2\2\2\u0090"+
		"\u0091\7-\2\2\u0091$\3\2\2\2\u0092\u0093\7/\2\2\u0093&\3\2\2\2\u0094\u0095"+
		"\7\61\2\2\u0095(\3\2\2\2\u0096\u0097\7,\2\2\u0097*\3\2\2\2\u0098\u0099"+
		"\5#\22\2\u0099\u009a\5#\22\2\u009a,\3\2\2\2\u009b\u009c\7#\2\2\u009c."+
		"\3\2\2\2\u009d\u009e\7?\2\2\u009e\60\3\2\2\2\u009f\u00a0\5\r\7\2\u00a0"+
		"\u00a1\5/\30\2\u00a1\62\3\2\2\2\u00a2\u00a3\7@\2\2\u00a3\64\3\2\2\2\u00a4"+
		"\u00a5\7>\2\2\u00a5\66\3\2\2\2\u00a6\u00a7\5\63\32\2\u00a7\u00a8\5/\30"+
		"\2\u00a88\3\2\2\2\u00a9\u00aa\5\65\33\2\u00aa\u00ab\5/\30\2\u00ab:\3\2"+
		"\2\2\u00ac\u00ad\5/\30\2\u00ad\u00ae\5/\30\2\u00ae<\3\2\2\2\u00af\u00b0"+
		"\5-\27\2\u00b0\u00b1\5/\30\2\u00b1>\3\2\2\2\u00b2\u00b3\5/\30\2\u00b3"+
		"\u00b4\5\63\32\2\u00b4@\3\2\2\2\u00b5\u00b6\5%\23\2\u00b6\u00b7\5\63\32"+
		"\2\u00b7B\3\2\2\2\u00b8\u00b9\7t\2\2\u00b9\u00ba\7g\2\2\u00ba\u00bb\7"+
		"v\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7t\2\2\u00bd\u00be\7p\2\2\u00beD"+
		"\3\2\2\2\u00bf\u00c0\7h\2\2\u00c0\u00c1\7w\2\2\u00c1\u00c2\7p\2\2\u00c2"+
		"F\3\2\2\2\u00c3\u00c4\7v\2\2\u00c4\u00c5\7{\2\2\u00c5\u00c6\7r\2\2\u00c6"+
		"\u00c7\7g\2\2\u00c7H\3\2\2\2\u00c8\u00c9\7n\2\2\u00c9\u00ca\7g\2\2\u00ca"+
		"\u00cb\7v\2\2\u00cbJ\3\2\2\2\u00cc\u00cd\7k\2\2\u00cd\u00ce\7h\2\2\u00ce"+
		"L\3\2\2\2\u00cf\u00d0\7g\2\2\u00d0\u00d1\7n\2\2\u00d1\u00d2\7u\2\2\u00d2"+
		"\u00d3\7g\2\2\u00d3N\3\2\2\2\u00d4\u00d5\7o\2\2\u00d5\u00d6\7w\2\2\u00d6"+
		"\u00d7\7v\2\2\u00d7\u00d8\7c\2\2\u00d8\u00d9\7d\2\2\u00d9\u00da\7n\2\2"+
		"\u00da\u00db\7g\2\2\u00dbP\3\2\2\2\u00dc\u00dd\7v\2\2\u00dd\u00de\7t\2"+
		"\2\u00de\u00df\7w\2\2\u00df\u00e0\7g\2\2\u00e0R\3\2\2\2\u00e1\u00e2\7"+
		"h\2\2\u00e2\u00e3\7c\2\2\u00e3\u00e4\7n\2\2\u00e4\u00e5\7u\2\2\u00e5\u00e6"+
		"\7g\2\2\u00e6T\3\2\2\2\u00e7\u00e8\7k\2\2\u00e8\u00e9\7o\2\2\u00e9\u00ea"+
		"\7r\2\2\u00ea\u00eb\7q\2\2\u00eb\u00ec\7t\2\2\u00ec\u00ed\7v\2\2\u00ed"+
		"V\3\2\2\2\u00ee\u00ef\7u\2\2\u00ef\u00f0\7k\2\2\u00f0\u00f1\7|\2\2\u00f1"+
		"\u00f2\7g\2\2\u00f2\u00f3\7q\2\2\u00f3\u00f4\7h\2\2\u00f4X\3\2\2\2\u00f5"+
		"\u00f6\7o\2\2\u00f6\u00f7\7q\2\2\u00f7\u00f8\7f\2\2\u00f8\u00f9\7w\2\2"+
		"\u00f9\u00fa\7n\2\2\u00fa\u00fb\7g\2\2\u00fbZ\3\2\2\2\u00fc\u00fd\7o\2"+
		"\2\u00fd\u00fe\7c\2\2\u00fe\u00ff\7v\2\2\u00ff\u0100\7e\2\2\u0100\u0101"+
		"\7j\2\2\u0101\\\3\2\2\2\u0102\u0103\7e\2\2\u0103\u0104\7c\2\2\u0104\u0105"+
		"\7u\2\2\u0105\u0106\7g\2\2\u0106^\3\2\2\2\u0107\u010b\t\4\2\2\u0108\u010a"+
		"\t\5\2\2\u0109\u0108\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010b"+
		"\u010c\3\2\2\2\u010c`\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u0114\7$\2\2\u010f"+
		"\u0113\n\6\2\2\u0110\u0111\7^\2\2\u0111\u0113\n\3\2\2\u0112\u010f\3\2"+
		"\2\2\u0112\u0110\3\2\2\2\u0113\u0116\3\2\2\2\u0114\u0112\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\u0117\3\2\2\2\u0116\u0114\3\2\2\2\u0117\u0123\7$"+
		"\2\2\u0118\u011e\7)\2\2\u0119\u011d\n\6\2\2\u011a\u011b\7^\2\2\u011b\u011d"+
		"\n\3\2\2\u011c\u0119\3\2\2\2\u011c\u011a\3\2\2\2\u011d\u0120\3\2\2\2\u011e"+
		"\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0121\3\2\2\2\u0120\u011e\3\2"+
		"\2\2\u0121\u0123\7)\2\2\u0122\u010e\3\2\2\2\u0122\u0118\3\2\2\2\u0123"+
		"b\3\2\2\2\u0124\u0126\t\7\2\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2\2\2"+
		"\u0127\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128d\3\2\2\2\u0129\u012a\5"+
		"c\62\2\u012a\u012b\7\60\2\2\u012b\u012c\5c\62\2\u012cf\3\2\2\2\16\2ik"+
		"pr\u010b\u0112\u0114\u011c\u011e\u0122\u0127\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}