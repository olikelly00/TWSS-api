const URL_PREFIX =
  window.location.toString().includes(':5000')
    ? 'http://localhost:8080'
    : '';

export default class Backend {
  // STATES: UNAUTH, PENDING_AUTH, AUTHED
  constructor() {
    this.state = "UNAUTH";
    this.backends = {};
    this.session = null;
  }

  subscribe(channel, callback) {
    if (this.state === "AUTHED") {
      this._subscribeToBackend(channel, callback);
    } else {
      throw new Error("Not authenticated");
    }
  }

  async isAuthenticated() {
    if (this.state === "UNAUTH") {
      let existingSession = JSON.parse(localStorage.getItem("session"));
      if (existingSession) {
        this.state = "PENDING_AUTH";
        let response = await fetch(URL_PREFIX + "/api/session", {
          headers: {
            'Authorization': existingSession.token
          }
        })
        if (response.status === 200) {
          this.state = "AUTHED";
          this.session = await response.json();
          return true;
        } else {
          this.state = "UNAUTH";
          this.authenticationPromise = null;
          return false;
        }
      } else {
        return false;
      }
    }
  }

  async authenticate(username, password) {
    this.state = "PENDING_AUTH";
    let response = await fetch(URL_PREFIX + "/api/session", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username,
        password: password
      })
    });
    if (response.status === 200) {
      let session = await response.json();
      this.state = "AUTHED";
      this.session = session;
      localStorage.setItem("session", JSON.stringify(session));
      return true;
    } else {
      this.state = "UNAUTH";
      this.authenticationPromise = null;
      return false;
    }
  }

  async signUp(username, password) {
    this.state = "PENDING_AUTH";
    let response = await fetch(URL_PREFIX + "/api/users", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username,
        password: password
      })
    });
    if (response.status === 200) {
      let session = await response.json();
      this.state = "AUTHED";
      this.session = session;
      localStorage.setItem("session", JSON.stringify(session));
      return true;
    } else {
      this.state = "UNAUTH";
      this.authenticationPromise = null;
      return false;
    }
  }

  async addPlaylist(playlist) {
    let response = await fetch(URL_PREFIX + '/api/playlists', {
      method: 'POST',
      headers: {
        'Authorization': this.session.token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(playlist)
    });

    if (response.status === 200) {
      if (this.backends["PLAYLIST_LIST"]) {
        this.backends["PLAYLIST_LIST"].refresh();
      }
      return true;
    } else {
      throw new Error('Failed to create playlist');
    }
  }

  async getUploadRequestInfo(filename, contentType) {
    const trackUploadKeyRes = await fetch(
      URL_PREFIX + '/api/storage/upload_key?' + new URLSearchParams({
        filename,
        contentType
      }), {
        headers: {
          'Authorization': this.session.token
        }
      });

    if (trackUploadKeyRes.status !== 200) {
      throw new Error('Failed to get track upload key');
    }

    return await trackUploadKeyRes.json();
  }

  async submitTrack(title, artist, publicUrl) {
    const trackSubmitRes = await fetch(URL_PREFIX + '/api/tracks', {
      method: 'POST',
      headers: {
        'Authorization': this.session.token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        title,
        artist,
        publicUrl,
      })
    });

    if (trackSubmitRes.status !== 200) {
      throw new Error('Failed to submit track');
    }

    this.backends["TRACK_LIST"].refresh();
    return true;
  }

  async deleteTrack(trackId) {
    const trackDeleteRes = await fetch(URL_PREFIX + '/api/tracks/' + trackId, {
      method: 'DELETE',
      headers: {
        'Authorization': this.session.token,
      }
    });

    if (trackDeleteRes.status !== 200) {
      throw new Error('Error deleting track');
    }

    this.backends["TRACK_LIST"].refresh();
    this.backends["PLAYLIST_LIST"].refresh();
  }

  async addTrackToPlaylist(playlistId, trackId) {
    const trackAddToPlaylistRes = await fetch(
      URL_PREFIX + '/api/playlists/' + playlistId + '/tracks', {
        method: 'PUT',
        headers: {
          'Authorization': this.session.token,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: trackId
        })
      });

    if (trackAddToPlaylistRes.status !== 200) {
      throw new Error('Error adding track to playlist');
    }

    this.backends["PLAYLIST_LIST"].refresh();
  }

  async removeTrackFromPlaylist(playlistId, trackId) {
    const res = await fetch(
      URL_PREFIX + '/api/playlists/' + playlistId + '/tracks/' + trackId, {
        method: 'DELETE',
        headers: {
          'Authorization': this.session.token,
        }
      });

    if (res.status !== 200) {
      throw new Error('Error deleting track from playlist');
    }
  }

  signOut() {
    this.state = "UNAUTH";
    this.session = null;
    localStorage.removeItem("session");
    this.backends = {};
  }

  _subscribeToBackend(channel, callback) {
    if (!this.backends[channel]) {
      if (channel == "TRACK_LIST") {
        this.backends[channel] = new TrackListBackend(this.session);
      } else if (channel == "USER") {
        this.backends[channel] = new UserBackend(this.session);
      } else if (channel == "PLAYLIST_LIST") {
        this.backends[channel] = new PlaylistListBackend(this.session);
      } else {
        throw new Error(`Invalid channel ${channel}`);
      }
    }
    this.backends[channel].subscribe(callback);
  }
}

class TrackListBackend {
  constructor(session) {
    this.session = session;
    this.state = "NOT_YET_USED";
    this.callbacks = [];
  }

  subscribe(callback) {
    if (this.state === "NOT_YET_USED") {
      this.callbacks.push(callback);
      this.loadData();
    } else if (this.state === "DATA_REQUESTED") {
      this.callbacks.push(callback);
    } else if (this.state === "DATA_LOADED") {
      callback(this.data);
    }
  }

  async loadData() {
    this.state = "DATA_REQUESTED";
    const response = await fetch(URL_PREFIX + '/api/tracks', {
      headers: {
        'Authorization': this.session.token
      }
    });
    this.data = await response.json();
    this.state = "DATA_LOADED";
    this.callbacks.forEach(callback => callback(this.data));
  }

  refresh() {
    this.loadData();
  }
}

class PlaylistListBackend {
  constructor(session) {
    this.session = session;
    this.state = "NOT_YET_USED";
    this.callbacks = [];
  }

  subscribe(callback) {
    if (this.state === "NOT_YET_USED") {
      this.state = "DATA_REQUESTED";
      this.callbacks.push(callback);
      this.loadData();
    } else if (this.state === "DATA_REQUESTED") {
      this.callbacks.push(callback);
    } else if (this.state === "DATA_LOADED") {
      this.callbacks.push(callback);
      callback(this.data);
    }
  }

  async loadData() {
    const response = await fetch(URL_PREFIX + '/api/playlists', {
      headers: {
        'Authorization': this.session.token
      }
    });
    this.data = await response.json();
    this.state = "DATA_LOADED";
    this.callbacks.forEach(callback => callback(this.data));
  }

  refresh() {
    this.loadData();
  }
}

class UserBackend {
  constructor(session) {
    this.session = session;
  }

  subscribe(callback) {
    callback(this.session.user)
  }
}
