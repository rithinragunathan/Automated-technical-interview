import React, { useState, useEffect, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Play, Send, RefreshCw, ChevronLeft } from 'lucide-react';
import CodeEditor from '../components/coding/CodeEditor';
import ProblemPanel from '../components/coding/ProblemPanel';
import TestCasePanel from '../components/coding/TestCasePanel';
import ExecutionStatus from '../components/coding/ExecutionStatus';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { getProblem } from '../api/problems';
import { runCode } from '../api/submissions';
import useWebSocket from '../hooks/useWebSocket';
import { SUBMISSION_STATUS, LANGUAGES } from '../utils/constants';
import toast from 'react-hot-toast';

const CodingPage = () => {
  const { id } = useParams();
  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Editor State
  const [language, setLanguage] = useState(LANGUAGES[0]);
  const [code, setCode] = useState('');
  
  // Execution State
  const [status, setStatus] = useState(SUBMISSION_STATUS.IDLE);
  const [results, setResults] = useState(null);
  const [metrics, setMetrics] = useState(null);
  
  const { connect, disconnect } = useWebSocket();

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const data = await getProblem(id);
        setProblem(data);
        // Set initial code based on language
        setCode(data.templates?.[language.id] || '// Write your code here\n');
      } catch (err) {
        // Mock data
        const mockProb = {
          id: id,
          title: "Two Sum",
          difficulty: "EASY",
          topic: "Arrays",
          description: "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.",
          examples: [
            { input: "nums = [2,7,11,15], target = 9", output: "[0,1]", explanation: "Because nums[0] + nums[1] == 9, we return [0, 1]." }
          ],
          constraints: ["2 <= nums.length <= 10^4", "-10^9 <= nums[i] <= 10^9", "-10^9 <= target <= 10^9"],
          templates: {
            JAVA: 'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        \n    }\n}'
          }
        };
        setProblem(mockProb);
        setCode(mockProb.templates.JAVA);
      } finally {
        setLoading(false);
      }
    };
    fetchProblem();
  }, [id]);

  // Handle WebSocket updates
  const handleWsMessage = (payload) => {
    if (payload.status) setStatus(payload.status);
    if (payload.results) setResults(payload.results);
    if (payload.metrics) setMetrics(payload.metrics);
    
    if (payload.status === SUBMISSION_STATUS.DONE || payload.status === SUBMISSION_STATUS.ERROR) {
      disconnect();
    }
  };

  const handleRun = async (isSubmit = false) => {
    if (!code.trim()) {
      toast.error('Code cannot be empty');
      return;
    }
    
    setStatus(SUBMISSION_STATUS.SUBMITTING);
    setResults(null);
    setMetrics(null);
    
    try {
      const response = await runCode({
        problemId: problem.id,
        code: code,
        languageId: language.id,
        isSubmit: isSubmit
      });
      
      const submissionId = response.submissionId || 'mock-sub-123';
      
      // Connect to WS for real-time updates
      connect(submissionId, handleWsMessage);
      
      // Fallback simulation if no real backend
      setTimeout(() => setStatus(SUBMISSION_STATUS.COMPILING), 1000);
      setTimeout(() => setStatus(SUBMISSION_STATUS.RUNNING), 2500);
      setTimeout(() => {
        setStatus(SUBMISSION_STATUS.DONE);
        setResults([
          { status: 'PASSED', input: '[2,7,11,15]\n9', expectedOutput: '[0,1]', actualOutput: '[0,1]', consoleOutput: '' },
          { status: 'PASSED', input: '[3,2,4]\n6', expectedOutput: '[1,2]', actualOutput: '[1,2]', consoleOutput: '' }
        ]);
        setMetrics({ timeMs: 42, memoryKb: 38400 });
        if (isSubmit) toast.success('Accepted! All test cases passed.');
      }, 4000);
      
    } catch (err) {
      setStatus(SUBMISSION_STATUS.ERROR);
      toast.error('Execution failed to start');
    }
  };

  const handleLanguageChange = (e) => {
    const lang = LANGUAGES.find(l => l.id === e.target.value);
    setLanguage(lang);
    if (problem?.templates?.[lang.id]) {
      setCode(problem.templates[lang.id]);
    }
  };

  const handleReset = () => {
    if (window.confirm('Are you sure you want to reset your code? This cannot be undone.')) {
      setCode(problem?.templates?.[language.id] || '');
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading IDE..." />;

  return (
    <div className="h-screen flex flex-col bg-[#0f1117] overflow-hidden">
      {/* Header */}
      <header className="h-14 bg-[#161b22] border-b border-[#30363d] flex items-center justify-between px-4 shrink-0">
        <div className="flex items-center">
          <Link to="/problems" className="mr-4 text-gray-400 hover:text-white transition-colors">
            <ChevronLeft className="w-5 h-5" />
          </Link>
          <span className="font-semibold text-white tracking-tight">{problem?.title}</span>
        </div>
        <div className="flex items-center space-x-4">
          <ExecutionStatus status={status} />
        </div>
      </header>

      {/* Main Layout */}
      <main className="flex-grow flex overflow-hidden">
        
        {/* Left Panel: Problem Description */}
        <div className="w-[40%] flex-shrink-0 relative">
          <ProblemPanel problem={problem} />
          {/* Resizer could go here */}
        </div>
        
        {/* Right Panel: Editor & Output */}
        <div className="w-[60%] flex flex-col min-w-[300px]">
          {/* Editor Toolbar */}
          <div className="h-12 bg-[#161b22] border-b border-[#30363d] flex items-center justify-between px-4 shrink-0">
            <div className="flex items-center">
              <select 
                value={language.id} 
                onChange={handleLanguageChange}
                className="bg-[#0f1117] border border-[#30363d] text-gray-300 text-sm rounded-md px-2 py-1 focus:outline-none focus:border-brand-500"
              >
                {LANGUAGES.map(lang => (
                  <option key={lang.id} value={lang.id}>{lang.name}</option>
                ))}
              </select>
              <button 
                onClick={handleReset}
                className="ml-3 p-1.5 text-gray-400 hover:text-white rounded-md hover:bg-[#30363d] transition-colors"
                title="Reset Code"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
            </div>
            <div className="flex items-center space-x-3">
              <button
                onClick={() => handleRun(false)}
                disabled={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
                className="px-4 py-1.5 bg-[#30363d] hover:bg-[#484f58] text-gray-200 text-sm font-medium rounded-md transition-colors flex items-center disabled:opacity-50"
              >
                <Play className="w-4 h-4 mr-1.5" /> Run Code
              </button>
              <button
                onClick={() => handleRun(true)}
                disabled={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
                className="px-4 py-1.5 bg-brand-600 hover:bg-brand-500 text-white text-sm font-medium rounded-md transition-colors flex items-center disabled:opacity-50"
              >
                <Send className="w-4 h-4 mr-1.5" /> Submit
              </button>
            </div>
          </div>

          {/* Editor Container */}
          <div className="flex-grow relative border-b border-[#30363d]">
            <CodeEditor 
              value={code} 
              onChange={setCode} 
              language={language.monacoId} 
              readOnly={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
            />
          </div>

          {/* Output Panel (fixed height or resizable, sticking to fixed for now) */}
          <div className="h-[250px] flex-shrink-0">
            <TestCasePanel results={results} status={status} metrics={metrics} />
          </div>
        </div>
      </main>
    </div>
  );
};

export default CodingPage;
