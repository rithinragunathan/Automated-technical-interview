import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Clock, CheckCircle2, ChevronRight, XCircle } from 'lucide-react';
import { getInterview, finishInterview } from '../api/interviews';
import { submitAnswer } from '../api/evaluation';
import AIInterviewer from '../components/interview/AIInterviewer';
import QuestionCard from '../components/interview/QuestionCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ConfirmDialog from '../components/common/ConfirmDialog';
import toast from 'react-hot-toast';

const LiveInterviewPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Timer state
  const [timeLeft, setTimeLeft] = useState(0);
  const [timerActive, setTimerActive] = useState(false);
  
  // Chat state
  const [messages, setMessages] = useState([
    { role: 'assistant', content: "Hello! I'm your AI interviewer. I'll be guiding you through this session today. Are you ready to begin with the first question?" }
  ]);
  const [isProcessing, setIsProcessing] = useState(false);
  
  // Question state
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [questionNumber, setQuestionNumber] = useState(1);
  const totalQuestions = 5;

  // Dialog state
  const [showEndDialog, setShowEndDialog] = useState(false);

  useEffect(() => {
    const initInterview = async () => {
      try {
        const data = await getInterview(id);
        setInterview(data);
        setTimeLeft(data.durationMinutes * 60);
        setTimerActive(true);
        // Set mock initial question
        setCurrentQuestion({
          id: 'q1',
          text: 'Can you explain the difference between a process and a thread?'
        });
      } catch (err) {
        // Mock data for development
        const mockData = { id, type: 'MIXED', topic: 'SYSTEM_DESIGN', durationMinutes: 45 };
        setInterview(mockData);
        setTimeLeft(mockData.durationMinutes * 60);
        setTimerActive(true);
        setCurrentQuestion({
          id: 'q1',
          text: 'Can you explain the difference between a process and a thread?'
        });
      } finally {
        setLoading(false);
      }
    };
    initInterview();
  }, [id]);

  useEffect(() => {
    let interval = null;
    if (timerActive && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft(timeLeft => timeLeft - 1);
      }, 1000);
    } else if (timerActive && timeLeft === 0) {
      setTimerActive(false);
      handleFinish(true); // Force finish on timeout
    }
    return () => clearInterval(interval);
  }, [timerActive, timeLeft]);

  const formatTime = (seconds) => {
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  const handleSendMessage = async (text) => {
    const newUserMsg = { role: 'user', content: text };
    setMessages(prev => [...prev, newUserMsg]);
    setIsProcessing(true);

    try {
      // API call to evaluate text
      // const response = await submitAnswer(id, currentQuestion.id, text);
      
      // Mock delay and response
      setTimeout(() => {
        const aiMsg = { 
          role: 'assistant', 
          content: "That's a good explanation. A process is an executing program with its own memory space, while threads exist within a process and share its memory. Now, let's move to the next topic.",
          feedback: "Good grasp of the fundamental difference."
        };
        setMessages(prev => [...prev, aiMsg]);
        setIsProcessing(false);
        
        if (questionNumber < totalQuestions) {
          setQuestionNumber(prev => prev + 1);
          setCurrentQuestion({
            id: `q${questionNumber + 1}`,
            text: 'How would you handle concurrency issues in a multithreaded application?'
          });
        }
      }, 1500);

    } catch (err) {
      toast.error('Failed to send message');
      setIsProcessing(false);
    }
  };

  const handleFinish = async (forced = false) => {
    try {
      // await finishInterview(id);
      toast.success(forced ? 'Time is up! Interview ended.' : 'Interview completed successfully!');
      navigate(`/interviews/${id}/report`);
    } catch (err) {
      navigate(`/interviews/${id}/report`); // fallback
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Preparing interview environment..." />;

  const isLowTime = timeLeft < 300; // less than 5 minutes

  return (
    <div className="h-screen flex flex-col bg-[#0f1117]">
      {/* Top Bar */}
      <header className="h-16 border-b border-[#30363d] bg-[#161b22] px-6 flex items-center justify-between shrink-0">
        <div className="flex items-center">
          <h1 className="text-lg font-semibold text-white tracking-tight">
            Live Session <span className="mx-2 text-[#30363d]">|</span> 
            <span className="text-gray-400 font-normal">{interview?.topic}</span>
          </h1>
        </div>
        <div className="flex items-center space-x-6">
          <div className="text-sm font-medium text-gray-400">
            Progress: <span className="text-white">{questionNumber}/{totalQuestions}</span>
          </div>
          <div className={`flex items-center text-lg font-mono font-bold ${isLowTime ? 'text-red-500 animate-pulse' : 'text-brand-400'}`}>
            <Clock className="w-5 h-5 mr-2" />
            {formatTime(timeLeft)}
          </div>
          <button
            onClick={() => setShowEndDialog(true)}
            className="px-4 py-2 bg-red-900/20 text-red-500 hover:bg-red-900/40 border border-red-900/50 rounded-md text-sm font-medium transition-colors"
          >
            End Interview
          </button>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-grow flex flex-col md:flex-row overflow-hidden p-4 gap-4">
        
        {/* Left Column: AI Interviewer */}
        <div className="w-full md:w-1/3 flex flex-col h-full">
          <AIInterviewer 
            currentQuestion={currentQuestion}
            messages={messages}
            onSendMessage={handleSendMessage}
            isProcessing={isProcessing}
          />
        </div>

        {/* Right Column: Interaction Space */}
        <div className="w-full md:w-2/3 flex flex-col h-full">
          {interview?.type === 'CODING' || interview?.type === 'MIXED' ? (
            <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-8 flex flex-col items-center justify-center h-full text-center">
              <Code className="w-16 h-16 text-gray-600 mb-4" />
              <h2 className="text-xl font-bold text-white mb-2">Coding Environment Required</h2>
              <p className="text-gray-400 mb-6 max-w-md">
                This question requires writing code. Please open the problem in the IDE to solve it.
              </p>
              <Link 
                to={`/problems/mock-prob-1`} 
                target="_blank"
                className="px-6 py-3 bg-brand-600 hover:bg-brand-500 text-white font-medium rounded-md flex items-center transition-colors"
              >
                Open IDE <ChevronRight className="w-5 h-5 ml-1" />
              </Link>
            </div>
          ) : (
            currentQuestion && (
              <QuestionCard 
                question={currentQuestion} 
                onSubmitAnswer={handleSendMessage}
                isSubmitting={isProcessing}
              />
            )
          )}
        </div>
      </main>

      {/* End Confirmation Dialog */}
      <ConfirmDialog 
        isOpen={showEndDialog}
        title="End Interview"
        message="Are you sure you want to end this interview? You cannot return to it once ended."
        confirmText="Yes, End Interview"
        cancelText="Resume"
        isDanger={true}
        onConfirm={() => handleFinish(false)}
        onCancel={() => setShowEndDialog(false)}
      />
    </div>
  );
};

export default LiveInterviewPage;
