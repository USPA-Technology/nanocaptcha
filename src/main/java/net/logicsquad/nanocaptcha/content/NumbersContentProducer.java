package net.logicsquad.nanocaptcha.content;


/**
 * TextProducer implementation that will return a series of numbers.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * 
 */
public class NumbersContentProducer implements ContentProducer {

    private static final int DEFAULT_LENGTH = 5;
    private static final char[] NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9' };

    private final ContentProducer _txtProd;
    
    public NumbersContentProducer() {
        this(DEFAULT_LENGTH);
    }
    
    public NumbersContentProducer(int length) {
        _txtProd = new DefaultContentProducer(length, NUMBERS);
    }

    @Override public String getContent() {
        return new StringBuffer(_txtProd.getContent()).toString();
    }
}
