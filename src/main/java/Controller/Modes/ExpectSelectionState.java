package Controller.Modes;

import Controller.Action.CompoundAction;
import Controller.Action.DeleteAction;
import Controller.Action.InsertAction;
import Controller.Action.NotifyAction;
import Controller.Action.Ranges.DelimiterRangeResolver;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import LanguageSupport.LanguageProvider;

class ExpectSelectionState implements ChordParserState {
    private final LanguageProvider provider = new LanguageProvider();
    private final ChordParser chordParser;
    private final String word;
    private final String[] corrections;

    public ExpectSelectionState(ChordParser chordParser, String word, String[] corrections) {
        this.chordParser = chordParser;
        this.word = word;
        this.corrections = corrections;
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        assert buffer.length() == 2: "buffer: " + buffer + " is not cool beans";

        char selection = buffer.toString().charAt(1);

        chordParser.changeState(new ExpectOperatorState(chordParser));
        buffer.clear();

        if (selection == '+') {
            return parseAddToDictionary(word, context);
        }

        if (!Character.isDigit(selection)) {
            return new ParseSnapshot(ParseStatus.INVALID, new NotifyAction(new Message(
                provider.get("tool/correct/invalidSelection"),
                MessageType.ERROR,
                TimeDuration.seconds(2)
            )));
        }

        int acceptedIndex = Character.getNumericValue(selection);
        String acceptedWord = corrections[acceptedIndex - 1]; // account for user-facing index starting at 1

        RangeResolver currentWord = DelimiterRangeResolver.makeWordUnderCursor();
        Range range = currentWord.resolve(context.viewPort(), context.textFile());

        return new ParseSnapshot(ParseStatus.FINISHED, new CompoundAction(
            new DeleteAction(currentWord),
            new InsertAction(acceptedWord, range.start())
        ));
    }

    private ParseSnapshot parseAddToDictionary(String word, ParsingContext context) {
        boolean success = context.textFile().addToDictionary(word);

        if (success) {
            return new ParseSnapshot(ParseStatus.FINISHED, new NotifyAction(new Message(
                String.format(provider.get("tool/correct/addedToDictionary"), word),
                MessageType.INFO,
                TimeDuration.seconds(2)
            )));
        }
        return new ParseSnapshot(ParseStatus.FINISHED, new NotifyAction(new Message(
                String.format(provider.get("tool/correct/failedToAddToDictionary"), word),
                MessageType.ERROR,
                TimeDuration.seconds(2)
        )));
    }
}
