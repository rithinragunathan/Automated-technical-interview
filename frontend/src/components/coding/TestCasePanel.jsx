import React, { useState } from 'react';
import { CheckCircle, XCircle, AlertTriangle, Clock, Server } from 'lucide-react';

const TestCasePanel = ({ results, status, metrics }) => {
  const [activeTab, setActiveTab] = useState('testcases');

  const tabs = [
    { id: 'testcases', label: 'Test Cases' },
    { id: 'output', label: 'Console Output' },
  ];

  const renderStatusIcon = (status) => {
    switch (status) {
      case 'PASSED': return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'FAILED': return <XCircle className="w-5 h-5 text-red-500" />;
      case 'ERROR': return <AlertTriangle className="w-5 h-5 text-yellow-500" />;
      default: return null;
    }
  };

  return (
    <div className="bg-[#161b22] border-t border-[#30363d] flex flex-col h-full text-sm">
      <div className="flex border-b border-[#30363d] bg-[#0f1117]">
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 font-medium transition-colors ${
              activeTab === tab.id 
                ? 'text-white border-b-2 border-brand-500 bg-[#161b22]' 
                : 'text-gray-400 hover:text-gray-300'
            }`}
          >
            {tab.label}
          </button>
        ))}
        
        {metrics && (
          <div className="ml-auto flex items-center px-4 space-x-4 text-xs text-gray-400">
            <span className="flex items-center"><Clock className="w-3 h-3 mr-1" /> {metrics.timeMs}ms</span>
            <span className="flex items-center"><Server className="w-3 h-3 mr-1" /> {metrics.memoryKb}KB</span>
          </div>
        )}
      </div>

      <div className="flex-grow overflow-y-auto p-4 font-mono">
        {status === 'IDLE' && !results && (
          <div className="text-gray-500 flex items-center justify-center h-full">
            Run your code to see results here.
          </div>
        )}

        {status !== 'IDLE' && status !== 'DONE' && status !== 'ERROR' && (
          <div className="text-brand-400 flex items-center justify-center h-full animate-pulse">
            Executing code...
          </div>
        )}

        {status === 'DONE' && activeTab === 'testcases' && results && (
          <div className="space-y-4">
            {results.map((result, idx) => (
              <div key={idx} className={`border rounded-md p-3 ${
                result.status === 'PASSED' ? 'bg-green-900/10 border-green-900/30' :
                result.status === 'FAILED' ? 'bg-red-900/10 border-red-900/30' :
                'bg-yellow-900/10 border-yellow-900/30'
              }`}>
                <div className="flex items-center justify-between mb-2">
                  <span className="font-semibold text-gray-200">Test Case {idx + 1}</span>
                  {renderStatusIcon(result.status)}
                </div>
                
                {result.status !== 'PASSED' && (
                  <div className="space-y-2 mt-3 text-xs">
                    <div>
                      <div className="text-gray-500 mb-1">Input:</div>
                      <div className="bg-[#0f1117] p-2 rounded text-gray-300 overflow-x-auto">{result.input}</div>
                    </div>
                    <div>
                      <div className="text-gray-500 mb-1">Expected Output:</div>
                      <div className="bg-[#0f1117] p-2 rounded text-green-400 overflow-x-auto">{result.expectedOutput}</div>
                    </div>
                    <div>
                      <div className="text-gray-500 mb-1">Actual Output:</div>
                      <div className="bg-[#0f1117] p-2 rounded text-red-400 overflow-x-auto">{result.actualOutput}</div>
                    </div>
                    {result.errorMessage && (
                      <div>
                        <div className="text-gray-500 mb-1">Error:</div>
                        <div className="bg-red-900/20 text-red-400 p-2 rounded whitespace-pre-wrap">{result.errorMessage}</div>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {status === 'DONE' && activeTab === 'output' && results && (
          <div className="bg-[#0f1117] p-4 rounded-md h-full overflow-y-auto">
            {results.some(r => r.consoleOutput) ? (
               results.map((result, idx) => result.consoleOutput && (
                 <div key={idx} className="mb-4">
                    <div className="text-gray-500 text-xs mb-1">From Test Case {idx + 1}:</div>
                    <pre className="text-gray-300 text-xs whitespace-pre-wrap">{result.consoleOutput}</pre>
                 </div>
               ))
            ) : (
              <span className="text-gray-600">No console output.</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default TestCasePanel;
