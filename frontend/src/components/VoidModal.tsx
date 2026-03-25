import { useEffect, useRef } from 'react';
import './VoidModal.css';

interface VoidModalProps {
  readonly message: string;
  readonly onConfirm: () => void;
  readonly onCancel: () => void;
  readonly confirmText?: string;
  readonly cancelText?: string;
}

export default function VoidModal({
  message,
  onConfirm,
  onCancel,
  confirmText = 'Confirm',
  cancelText = 'Turn Back',
}: VoidModalProps) {
  const overlayRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onCancel();
    };
    globalThis.addEventListener('keydown', handleKey);
    return () => globalThis.removeEventListener('keydown', handleKey);
  }, [onCancel]);

  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === overlayRef.current) onCancel();
  };

  return (
    <div
      className="void-modal-overlay"
      ref={overlayRef}
      role="dialog"
      aria-modal="true"
      onClick={handleOverlayClick}
      onKeyDown={(e) => { if (e.key === 'Escape') onCancel(); }}
    >
      <div className="void-modal">
        <div className="void-modal-sigil">⛧</div>
        <p className="void-modal-message">{message}</p>
        <div className="void-modal-actions">
          <button className="btn-danger void-modal-confirm" onClick={onConfirm}>
            {confirmText}
          </button>
          <button className="btn-small void-modal-cancel" onClick={onCancel}>
            {cancelText}
          </button>
        </div>
      </div>
    </div>
  );
}
