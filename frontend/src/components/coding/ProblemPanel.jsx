import React from 'react';
import Badge from '../common/Badge';
import { getDifficultyColor } from '../../utils/scoreHelpers';

const ProblemPanel = ({ problem }) => {
  if (!problem) return null;

  const examples = typeof problem.examples === 'string'
    ? (() => {
        try {
          return JSON.parse(problem.examples);
        } catch {
          return [];
        }
      })()
    : problem.examples || [];
  const constraints = typeof problem.constraints === 'string'
    ? problem.constraints.split('\n').filter(Boolean)
    : problem.constraints || [];

  return (
    <div className="h-full bg-[#161b22] border-r border-[#30363d] overflow-y-auto custom-scrollbar">
      <div className="p-6">
        <div className="flex items-start justify-between mb-4">
          <h1 className="text-2xl font-bold text-white">{problem.title}</h1>
          <Badge 
            variant={getDifficultyColor(problem.difficulty)} 
            text={problem.difficulty} 
          />
        </div>
        
        <div className="flex flex-wrap gap-2 mb-6">
          <Badge variant="neutral" text={problem.topic} />
          {problem.tags && problem.tags.map((tag, idx) => (
            <Badge key={idx} variant="neutral" text={tag} />
          ))}
        </div>

        <div className="prose prose-invert max-w-none text-gray-300 text-sm leading-relaxed mb-8">
          {/* In a real app, render Markdown here */}
          <div className="whitespace-pre-wrap">{problem.description}</div>
        </div>

        {(problem.inputFormat || problem.outputFormat) && (
          <div className="space-y-3 mb-8">
            {problem.inputFormat && <div><h3 className="text-sm font-semibold text-gray-400 mb-1">Input Format</h3><p className="text-sm text-gray-300 whitespace-pre-wrap">{problem.inputFormat}</p></div>}
            {problem.outputFormat && <div><h3 className="text-sm font-semibold text-gray-400 mb-1">Output Format</h3><p className="text-sm text-gray-300 whitespace-pre-wrap">{problem.outputFormat}</p></div>}
          </div>
        )}

        {examples.length > 0 && (
          <div className="space-y-6 mb-8">
            <h3 className="text-lg font-semibold text-white border-b border-[#30363d] pb-2">Examples</h3>
            {examples.map((example, idx) => (
              <div key={idx} className="bg-[#0f1117] rounded-md p-4 border border-[#30363d]">
                <div className="mb-2">
                  <span className="font-semibold text-gray-400">Input: </span>
                  <span className="font-mono text-sm text-gray-200">{example.input}</span>
                </div>
                <div className="mb-2">
                  <span className="font-semibold text-gray-400">Output: </span>
                  <span className="font-mono text-sm text-green-400">{example.output}</span>
                </div>
                {example.explanation && (
                  <div>
                    <span className="font-semibold text-gray-400">Explanation: </span>
                    <span className="text-sm text-gray-300">{example.explanation}</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {constraints.length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-white border-b border-[#30363d] pb-2 mb-4">Constraints</h3>
            <ul className="list-disc pl-5 space-y-2 text-sm text-gray-300 font-mono">
              {constraints.map((constraint, idx) => (
                <li key={idx}>{constraint}</li>
              ))}
            </ul>
          </div>
        )}

        {problem.publicTestCases?.length > 0 && (
          <div className="mt-8">
            <h3 className="text-lg font-semibold text-white border-b border-[#30363d] pb-2 mb-4">Test Cases</h3>
            <div className="space-y-4">
              {problem.publicTestCases.map((testCase, idx) => (
                <div key={testCase.id || idx} className="bg-[#0f1117] rounded-md p-4 border border-[#30363d]">
                  <div className="text-sm font-semibold text-gray-200 mb-2">Example {idx + 1}{testCase.description ? `: ${testCase.description}` : ''}</div>
                  <div className="text-xs text-gray-500 mb-1">Input</div>
                  <pre className="text-sm text-gray-300 whitespace-pre-wrap">{testCase.input}</pre>
                  <div className="text-xs text-gray-500 mt-3 mb-1">Expected Output</div>
                  <pre className="text-sm text-green-400 whitespace-pre-wrap">{testCase.expectedOutput}</pre>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProblemPanel;
