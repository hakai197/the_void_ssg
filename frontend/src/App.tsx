import { Routes, Route } from 'react-router-dom';
import './App.css';
import SiteListPage from './pages/SiteListPage';
import SiteDetailPage from './pages/SiteDetailPage';
import EntryDetailPage from './pages/EntryDetailPage';
import EntryCreatePage from './pages/EntryCreatePage';

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <a href="/" className="header-link">
          <h1>🌀 THE VOID</h1>
          <p className="subtitle">Static Site Generator with Cosmic Horror</p>
        </a>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<SiteListPage />} />
          <Route path="/site/:siteId" element={<SiteDetailPage />} />
          <Route path="/site/:siteId/entries/new" element={<EntryCreatePage />} />
          <Route path="/site/:siteId/entries/:slug" element={<EntryDetailPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;