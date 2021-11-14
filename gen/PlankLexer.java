// Generated from /home/gabi/Projects/kotlin/jplank/grammar/src/commonAntlr/antlr/PlankLexer.g4 by ANTLR 4.9.2
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
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, NEWLINE=2, SEMICOLON=3, COMMA=4, COLON=5, BAR=6, LPAREN=7, RPAREN=8, 
		LBRACE=9, RBRACE=10, LBRACKET=11, RBRACKET=12, ADD=13, SUB=14, DIV=15, 
		TIMES=16, CONCAT=17, AMPERSTAND=18, BANG=19, ASSIGN=20, EQUAL=21, EQ=22, 
		NEQ=23, GTE=24, GT=25, LT=26, LTE=27, APOSTROPHE=28, DOUBLE_ARROW_LEFT=29, 
		ARROW_LEFT=30, DOT=31, RETURN=32, FUN=33, TYPE=34, LET=35, IF=36, ELSE=37, 
		MUTABLE=38, TRUE=39, FALSE=40, IMPORT=41, NATIVE=42, SIZEOF=43, MODULE=44, 
		MATCH=45, CASE=46, IDENTIFIER=47, STRING=48, INT=49, DECIMAL=50;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WS", "NEWLINE", "SEMICOLON", "COMMA", "COLON", "BAR", "LPAREN", "RPAREN", 
			"LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "ADD", "SUB", "DIV", "TIMES", 
			"CONCAT", "AMPERSTAND", "BANG", "ASSIGN", "EQUAL", "EQ", "NEQ", "GTE", 
			"GT", "LT", "LTE", "APOSTROPHE", "DOUBLE_ARROW_LEFT", "ARROW_LEFT", "DOT", 
			"RETURN", "FUN", "TYPE", "LET", "IF", "ELSE", "MUTABLE", "TRUE", "FALSE", 
			"IMPORT", "NATIVE", "SIZEOF", "MODULE", "MATCH", "CASE", "IDENTIFIER", 
			"STRING", "INT", "DECIMAL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "','", "':'", "'|'", "'('", "')'", "'{'", "'}'", 
			"'['", "']'", "'+'", "'-'", "'/'", "'*'", "'++'", "'&'", "'!'", "':='", 
			"'='", "'=='", "'!='", "'>='", "'>'", "'<'", "'<='", "'''", "'=>'", "'->'", 
			"'.'", "'return'", "'fun'", "'type'", "'let'", "'if'", "'else'", "'mutable'", 
			"'true'", "'false'", "'import'", "'native'", "'sizeof'", "'module'", 
			"'match'", "'case'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "NEWLINE", "SEMICOLON", "COMMA", "COLON", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "ADD", "SUB", "DIV", 
			"TIMES", "CONCAT", "AMPERSTAND", "BANG", "ASSIGN", "EQUAL", "EQ", "NEQ", 
			"GTE", "GT", "LT", "LTE", "APOSTROPHE", "DOUBLE_ARROW_LEFT", "ARROW_LEFT", 
			"DOT", "RETURN", "FUN", "TYPE", "LET", "IF", "ELSE", "MUTABLE", "TRUE", 
			"FALSE", "IMPORT", "NATIVE", "SIZEOF", "MODULE", "MATCH", "CASE", "IDENTIFIER", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u0135\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2"+
		"\3\2\6\2j\n\2\r\2\16\2k\3\2\3\2\3\3\6\3q\n\3\r\3\16\3r\3\4\6\4v\n\4\r"+
		"\4\16\4w\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f"+
		"\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3"+
		"\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3"+
		"\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36\3"+
		"\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3#"+
		"\3#\3#\3#\3#\3$\3$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3*\3*\3+\3"+
		"+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3-\3-\3.\3.\3.\3"+
		".\3.\3.\3/\3/\3/\3/\3/\3\60\3\60\7\60\u0112\n\60\f\60\16\60\u0115\13\60"+
		"\3\61\3\61\3\61\3\61\7\61\u011b\n\61\f\61\16\61\u011e\13\61\3\61\3\61"+
		"\3\61\3\61\3\61\7\61\u0125\n\61\f\61\16\61\u0128\13\61\3\61\5\61\u012b"+
		"\n\61\3\62\6\62\u012e\n\62\r\62\16\62\u012f\3\63\3\63\3\63\3\63\2\2\64"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37"+
		"= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64\3\2\b\4\2\13\13"+
		"\"\"\4\2\f\f\17\17\5\2C\\aac|\6\2\62;C\\aac|\6\2\f\f\17\17$$^^\3\2\62"+
		";\2\u013f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S"+
		"\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2"+
		"\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\3i\3\2\2\2\5p\3\2\2\2\7u\3\2\2\2"+
		"\ty\3\2\2\2\13{\3\2\2\2\r}\3\2\2\2\17\177\3\2\2\2\21\u0081\3\2\2\2\23"+
		"\u0083\3\2\2\2\25\u0085\3\2\2\2\27\u0087\3\2\2\2\31\u0089\3\2\2\2\33\u008b"+
		"\3\2\2\2\35\u008d\3\2\2\2\37\u008f\3\2\2\2!\u0091\3\2\2\2#\u0093\3\2\2"+
		"\2%\u0096\3\2\2\2\'\u0098\3\2\2\2)\u009a\3\2\2\2+\u009d\3\2\2\2-\u009f"+
		"\3\2\2\2/\u00a2\3\2\2\2\61\u00a5\3\2\2\2\63\u00a8\3\2\2\2\65\u00aa\3\2"+
		"\2\2\67\u00ac\3\2\2\29\u00af\3\2\2\2;\u00b1\3\2\2\2=\u00b4\3\2\2\2?\u00b7"+
		"\3\2\2\2A\u00b9\3\2\2\2C\u00c0\3\2\2\2E\u00c4\3\2\2\2G\u00c9\3\2\2\2I"+
		"\u00cd\3\2\2\2K\u00d0\3\2\2\2M\u00d5\3\2\2\2O\u00dd\3\2\2\2Q\u00e2\3\2"+
		"\2\2S\u00e8\3\2\2\2U\u00ef\3\2\2\2W\u00f6\3\2\2\2Y\u00fd\3\2\2\2[\u0104"+
		"\3\2\2\2]\u010a\3\2\2\2_\u010f\3\2\2\2a\u012a\3\2\2\2c\u012d\3\2\2\2e"+
		"\u0131\3\2\2\2gj\t\2\2\2hj\5\5\3\2ig\3\2\2\2ih\3\2\2\2jk\3\2\2\2ki\3\2"+
		"\2\2kl\3\2\2\2lm\3\2\2\2mn\b\2\2\2n\4\3\2\2\2oq\t\3\2\2po\3\2\2\2qr\3"+
		"\2\2\2rp\3\2\2\2rs\3\2\2\2s\6\3\2\2\2tv\7=\2\2ut\3\2\2\2vw\3\2\2\2wu\3"+
		"\2\2\2wx\3\2\2\2x\b\3\2\2\2yz\7.\2\2z\n\3\2\2\2{|\7<\2\2|\f\3\2\2\2}~"+
		"\7~\2\2~\16\3\2\2\2\177\u0080\7*\2\2\u0080\20\3\2\2\2\u0081\u0082\7+\2"+
		"\2\u0082\22\3\2\2\2\u0083\u0084\7}\2\2\u0084\24\3\2\2\2\u0085\u0086\7"+
		"\177\2\2\u0086\26\3\2\2\2\u0087\u0088\7]\2\2\u0088\30\3\2\2\2\u0089\u008a"+
		"\7_\2\2\u008a\32\3\2\2\2\u008b\u008c\7-\2\2\u008c\34\3\2\2\2\u008d\u008e"+
		"\7/\2\2\u008e\36\3\2\2\2\u008f\u0090\7\61\2\2\u0090 \3\2\2\2\u0091\u0092"+
		"\7,\2\2\u0092\"\3\2\2\2\u0093\u0094\7-\2\2\u0094\u0095\7-\2\2\u0095$\3"+
		"\2\2\2\u0096\u0097\7(\2\2\u0097&\3\2\2\2\u0098\u0099\7#\2\2\u0099(\3\2"+
		"\2\2\u009a\u009b\7<\2\2\u009b\u009c\7?\2\2\u009c*\3\2\2\2\u009d\u009e"+
		"\7?\2\2\u009e,\3\2\2\2\u009f\u00a0\7?\2\2\u00a0\u00a1\7?\2\2\u00a1.\3"+
		"\2\2\2\u00a2\u00a3\7#\2\2\u00a3\u00a4\7?\2\2\u00a4\60\3\2\2\2\u00a5\u00a6"+
		"\7@\2\2\u00a6\u00a7\7?\2\2\u00a7\62\3\2\2\2\u00a8\u00a9\7@\2\2\u00a9\64"+
		"\3\2\2\2\u00aa\u00ab\7>\2\2\u00ab\66\3\2\2\2\u00ac\u00ad\7>\2\2\u00ad"+
		"\u00ae\7?\2\2\u00ae8\3\2\2\2\u00af\u00b0\7)\2\2\u00b0:\3\2\2\2\u00b1\u00b2"+
		"\7?\2\2\u00b2\u00b3\7@\2\2\u00b3<\3\2\2\2\u00b4\u00b5\7/\2\2\u00b5\u00b6"+
		"\7@\2\2\u00b6>\3\2\2\2\u00b7\u00b8\7\60\2\2\u00b8@\3\2\2\2\u00b9\u00ba"+
		"\7t\2\2\u00ba\u00bb\7g\2\2\u00bb\u00bc\7v\2\2\u00bc\u00bd\7w\2\2\u00bd"+
		"\u00be\7t\2\2\u00be\u00bf\7p\2\2\u00bfB\3\2\2\2\u00c0\u00c1\7h\2\2\u00c1"+
		"\u00c2\7w\2\2\u00c2\u00c3\7p\2\2\u00c3D\3\2\2\2\u00c4\u00c5\7v\2\2\u00c5"+
		"\u00c6\7{\2\2\u00c6\u00c7\7r\2\2\u00c7\u00c8\7g\2\2\u00c8F\3\2\2\2\u00c9"+
		"\u00ca\7n\2\2\u00ca\u00cb\7g\2\2\u00cb\u00cc\7v\2\2\u00ccH\3\2\2\2\u00cd"+
		"\u00ce\7k\2\2\u00ce\u00cf\7h\2\2\u00cfJ\3\2\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"\u00d2\7n\2\2\u00d2\u00d3\7u\2\2\u00d3\u00d4\7g\2\2\u00d4L\3\2\2\2\u00d5"+
		"\u00d6\7o\2\2\u00d6\u00d7\7w\2\2\u00d7\u00d8\7v\2\2\u00d8\u00d9\7c\2\2"+
		"\u00d9\u00da\7d\2\2\u00da\u00db\7n\2\2\u00db\u00dc\7g\2\2\u00dcN\3\2\2"+
		"\2\u00dd\u00de\7v\2\2\u00de\u00df\7t\2\2\u00df\u00e0\7w\2\2\u00e0\u00e1"+
		"\7g\2\2\u00e1P\3\2\2\2\u00e2\u00e3\7h\2\2\u00e3\u00e4\7c\2\2\u00e4\u00e5"+
		"\7n\2\2\u00e5\u00e6\7u\2\2\u00e6\u00e7\7g\2\2\u00e7R\3\2\2\2\u00e8\u00e9"+
		"\7k\2\2\u00e9\u00ea\7o\2\2\u00ea\u00eb\7r\2\2\u00eb\u00ec\7q\2\2\u00ec"+
		"\u00ed\7t\2\2\u00ed\u00ee\7v\2\2\u00eeT\3\2\2\2\u00ef\u00f0\7p\2\2\u00f0"+
		"\u00f1\7c\2\2\u00f1\u00f2\7v\2\2\u00f2\u00f3\7k\2\2\u00f3\u00f4\7x\2\2"+
		"\u00f4\u00f5\7g\2\2\u00f5V\3\2\2\2\u00f6\u00f7\7u\2\2\u00f7\u00f8\7k\2"+
		"\2\u00f8\u00f9\7|\2\2\u00f9\u00fa\7g\2\2\u00fa\u00fb\7q\2\2\u00fb\u00fc"+
		"\7h\2\2\u00fcX\3\2\2\2\u00fd\u00fe\7o\2\2\u00fe\u00ff\7q\2\2\u00ff\u0100"+
		"\7f\2\2\u0100\u0101\7w\2\2\u0101\u0102\7n\2\2\u0102\u0103\7g\2\2\u0103"+
		"Z\3\2\2\2\u0104\u0105\7o\2\2\u0105\u0106\7c\2\2\u0106\u0107\7v\2\2\u0107"+
		"\u0108\7e\2\2\u0108\u0109\7j\2\2\u0109\\\3\2\2\2\u010a\u010b\7e\2\2\u010b"+
		"\u010c\7c\2\2\u010c\u010d\7u\2\2\u010d\u010e\7g\2\2\u010e^\3\2\2\2\u010f"+
		"\u0113\t\4\2\2\u0110\u0112\t\5\2\2\u0111\u0110\3\2\2\2\u0112\u0115\3\2"+
		"\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114`\3\2\2\2\u0115\u0113"+
		"\3\2\2\2\u0116\u011c\7$\2\2\u0117\u011b\n\6\2\2\u0118\u0119\7^\2\2\u0119"+
		"\u011b\n\3\2\2\u011a\u0117\3\2\2\2\u011a\u0118\3\2\2\2\u011b\u011e\3\2"+
		"\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\3\2\2\2\u011e"+
		"\u011c\3\2\2\2\u011f\u012b\7$\2\2\u0120\u0126\7)\2\2\u0121\u0125\n\6\2"+
		"\2\u0122\u0123\7^\2\2\u0123\u0125\n\3\2\2\u0124\u0121\3\2\2\2\u0124\u0122"+
		"\3\2\2\2\u0125\u0128\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127"+
		"\u0129\3\2\2\2\u0128\u0126\3\2\2\2\u0129\u012b\7)\2\2\u012a\u0116\3\2"+
		"\2\2\u012a\u0120\3\2\2\2\u012bb\3\2\2\2\u012c\u012e\t\7\2\2\u012d\u012c"+
		"\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130"+
		"d\3\2\2\2\u0131\u0132\5c\62\2\u0132\u0133\7\60\2\2\u0133\u0134\5c\62\2"+
		"\u0134f\3\2\2\2\17\2ikprw\u0113\u011a\u011c\u0124\u0126\u012a\u012f\3"+
		"\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}