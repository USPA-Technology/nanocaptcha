package net.logicsquad.nanocaptcha.audio;

import java.io.*;

import javax.sound.sampled.*;

/**
 * <p>
 * Class representing a sound sample, typically read in from a file. Note that
 * at this time this class only supports wav files with the following
 * characteristics:
 * </p>
 * 
 * <ul>
 * <li>Sample rate: 16KHz</li>
 * <li>Sample size: 16 bits</li>
 * <li>Channels: 1</li>
 * <li>Signed: true</li>
 * <li>Big Endian: false</li>
 * </ul>
 * 
 * <p>
 * Data files in other formats will cause an
 * <code>IllegalArgumentException</code> to be thrown.
 * </p>
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * @author <a href="mailto:paulh@logicsquad.net">Paul Hoadley</a>
 * @since 1.0
 */
public class Sample {

    public static final AudioFormat SC_AUDIO_FORMAT = new AudioFormat(
            16000, // sample rate
            16, // sample size in bits
            1, // channels
            true, // signed?
            false); // big endian?;

    private final AudioInputStream _audioInputStream;

    public Sample(String filename) {
		this(Sample.class.getResourceAsStream(filename));
    }

    public Sample(InputStream is) {
        if (is instanceof AudioInputStream) {
            _audioInputStream = (AudioInputStream) is;
            return;
        }

        try {
            _audioInputStream = AudioSystem.getAudioInputStream(is);

        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkFormat(_audioInputStream.getFormat());
    }

    public AudioInputStream getAudioInputStream() {
        return _audioInputStream;
    }

    public AudioFormat getFormat() {
        return _audioInputStream.getFormat();
    }

    /**
     * Return the number of samples of all channels
     * 
     * @return The number of samples for all channels
     */
    public long getSampleCount() {
        long total = (_audioInputStream.getFrameLength()
                * getFormat().getFrameSize() * 8)
                / getFormat().getSampleSizeInBits();
        return total / getFormat().getChannels();
    }

    public double[] getInterleavedSamples() {
        double[] samples = new double[(int) getSampleCount()];
        try {
            getInterleavedSamples(0, getSampleCount(), samples);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return samples;
    }

    /**
     * Get the interleaved decoded samples for all channels, from sample index
     * <code>begin</code> (included) to sample index <code>end</code> (excluded)
     * and copy them into <code>samples</code>. <code>end</code> must not exceed
     * <code>getSampleCount()</code>, and the number of samples must not be so
     * large that the associated byte array cannot be allocated
     * 
     * @param begin
     * @param end
     * @param samples
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public double[] getInterleavedSamples(long begin, long end, double[] samples)
            throws IOException, IllegalArgumentException {
        long nbSamples = end - begin;
        long nbBytes = nbSamples * (getFormat().getSampleSizeInBits() / 8)
                * getFormat().getChannels();
        if (nbBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Too many samples. Try using a smaller wav.");
        }
        // allocate a byte buffer
        byte[] inBuffer = new byte[(int) nbBytes];
        // read bytes from audio file
        _audioInputStream.read(inBuffer, 0, inBuffer.length);
        // decode bytes into samples.
        decodeBytes(inBuffer, samples);

        return samples;
    }

    // Decode bytes of audioBytes into audioSamples
    public void decodeBytes(byte[] audioBytes, double[] audioSamples) {
        int sampleSizeInBytes = getFormat().getSampleSizeInBits() / 8;
        int[] sampleBytes = new int[sampleSizeInBytes];
        int k = 0; // index in audioBytes
        for (int i = 0; i < audioSamples.length; i++) {
            // collect sample byte in big-endian order
            if (getFormat().isBigEndian()) {
                // bytes start with MSB
                for (int j = 0; j < sampleSizeInBytes; j++) {
                    sampleBytes[j] = audioBytes[k++];
                }
            } else {
                // bytes start with LSB
                for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                    sampleBytes[j] = audioBytes[k++];
                    if (sampleBytes[j] != 0)
                        j = j + 0;
                }
            }
            // get integer value from bytes
            int ival = 0;
            for (int j = 0; j < sampleSizeInBytes; j++) {
                ival += sampleBytes[j];
                if (j < sampleSizeInBytes - 1)
                    ival <<= 8;
            }
            // decode value
            double ratio = Math.pow(2., getFormat().getSampleSizeInBits() - 1);
            double val = ((double) ival) / ratio;
            audioSamples[i] = val;
        }
    }


    @Override public String toString() {
        return "[Sample] samples: " + getSampleCount() + ", format: "
                + getFormat();
    }

    private static final void checkFormat(AudioFormat af) {
        if (!af.matches(SC_AUDIO_FORMAT)) {
            throw new IllegalArgumentException(
                    "Unsupported audio format.\nReceived: " + af.toString()
                            + "\nExpected: " + SC_AUDIO_FORMAT);

        }
    }
}
