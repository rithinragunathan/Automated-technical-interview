import React from 'react';
import Editor from '@monaco-editor/react';
import { Loader2 } from 'lucide-react';

const CodeEditor = ({ value, onChange, language = 'java', readOnly = false, height = '100%' }) => {
  const handleEditorChange = (value) => {
    if (onChange) {
      onChange(value);
    }
  };

  const loadingFallback = (
    <div className="flex flex-col items-center justify-center h-full w-full bg-[#1e1e1e] text-gray-400">
      <Loader2 className="w-8 h-8 animate-spin mb-4" />
      <p>Loading editor...</p>
    </div>
  );

  return (
    <div className="w-full border border-[#30363d] rounded-none overflow-hidden" style={{ height }}>
      <Editor
        height="100%"
        language={language}
        theme="vs-dark"
        value={value}
        onChange={handleEditorChange}
        loading={loadingFallback}
        options={{
          readOnly,
          minimap: { enabled: false },
          fontSize: 14,
          fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
          lineHeight: 24,
          padding: { top: 16, bottom: 16 },
          scrollBeyondLastLine: false,
          smoothScrolling: true,
          cursorBlinking: "smooth",
          cursorSmoothCaretAnimation: "on",
          formatOnPaste: true,
        }}
      />
    </div>
  );
};

export default CodeEditor;
