import { useCallback, useEffect, useRef, useState } from 'react';

const getSpeechRecognitionConstructor = () => {
  if (typeof window === 'undefined') return null;
  return window.SpeechRecognition || window.webkitSpeechRecognition || null;
};

const getRecognitionErrorMessage = (code) => {
  switch (code) {
    case 'not-allowed':
    case 'service-not-allowed':
      return 'Microphone access was denied. Allow microphone access in your browser settings and try again.';
    case 'audio-capture':
      return 'No microphone was found. Connect or enable a microphone and try again.';
    case 'no-speech':
      return 'No speech was detected. Try speaking closer to your microphone.';
    case 'network':
      return 'Speech recognition is unavailable because of a network error.';
    default:
      return `Voice input failed (${code}). You can still type your answer.`;
  }
};

const useSpeechRecognition = (value, onChange) => {
  const [isSupported, setIsSupported] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [error, setError] = useState('');
  const recognitionRef = useRef(null);
  const valueRef = useRef(value);
  const onChangeRef = useRef(onChange);

  useEffect(() => {
    setIsSupported(Boolean(getSpeechRecognitionConstructor()));
  }, []);

  useEffect(() => {
    valueRef.current = value;
    onChangeRef.current = onChange;
  }, [value, onChange]);

  const stop = useCallback(() => {
    const recognition = recognitionRef.current;
    if (!recognition) return;
    try {
      recognition.stop();
    } catch (recognitionError) {
      setError(recognitionError.message || 'Could not stop voice input.');
    }
  }, []);

  const start = useCallback(() => {
    const SpeechRecognition = getSpeechRecognitionConstructor();
    if (!SpeechRecognition) {
      setError('Voice input is not supported in this browser. You can type your answer instead.');
      return;
    }

    const previousRecognition = recognitionRef.current;
    if (previousRecognition) {
      try {
        previousRecognition.abort();
      } catch (recognitionError) {
        setError(recognitionError.message || 'Could not restart voice input.');
        recognitionRef.current = null;
      }
    }
    setError('');

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = navigator.language || 'en-US';
    const startingText = valueRef.current.trim();

    recognition.onresult = (event) => {
      const transcript = Array.from(event.results)
        .map(result => result[0]?.transcript || '')
        .join(' ')
        .trim();
      onChangeRef.current([startingText, transcript].filter(Boolean).join(' '));
    };
    recognition.onerror = (event) => {
      setError(getRecognitionErrorMessage(event.error));
      setIsListening(false);
    };
    recognition.onend = () => {
      if (recognitionRef.current === recognition) {
        setIsListening(false);
        recognitionRef.current = null;
      }
    };

    recognitionRef.current = recognition;
    try {
      recognition.start();
      setIsListening(true);
    } catch (recognitionError) {
      recognitionRef.current = null;
      setIsListening(false);
      setError(recognitionError.message || 'Could not start voice input. You can type your answer instead.');
    }
  }, []);

  useEffect(() => () => {
    try {
      recognitionRef.current?.abort();
    } catch (recognitionError) {
      console.warn('Could not stop browser speech recognition during cleanup:', recognitionError);
      recognitionRef.current = null;
    }
  }, []);

  return { error, isListening, isSupported, start, stop };
};

export default useSpeechRecognition;
