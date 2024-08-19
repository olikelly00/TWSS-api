<script>
  import { createEventDispatcher } from 'svelte';
  import PlaylistLinks from './PlaylistLinks.svelte';
  import Playlist from './Playlist.svelte';
  import Library from './Library.svelte';

  const dispatch = createEventDispatcher();

  export let backend;
  let page = { screen: 'library', payload: null };
  let user = null;

  backend.subscribe("USER", newUser => {
    user = newUser;
  })

  function handleShowPlaylist(evt) {
    page = { screen: 'playlist', payload: evt.detail.id };
  }

</script>

<div class="grid">
  <aside class="sidebar">
    <h1>Aceplay</h1>
    {#if user}
      Hello {user.username}!
    {/if}

    <nav>
      <ul>
        <PlaylistLinks backend={backend} on:showPlaylist={handleShowPlaylist} />
        <li><button on:click={() => page = { screen: 'library', payload: null }}>Library</button></li>
        <li><button on:click={() => dispatch("signOut")}>Sign Out</button></li>
      </ul>
    </nav>
  </aside>
  <main class="main">
    {#if page.screen == 'library'}
      <h2>Library</h2>
      <Library backend={backend} />
    {:else if page.screen == 'playlist'}
      <h2>Playlist</h2>
      <Playlist backend={backend} playlistId={page.payload}  />
    {:else}
      <h2>404 Not Found</h2>
      <p>No such page {page}</p>
    {/if}
  </main>
</div>

<style>
  .grid {
    display: grid;
    grid-template-columns: 1fr 3fr;
    grid-template-rows: auto;
    grid-template-areas: "sidebar main";
    grid-gap: 1rem;
  }
  .sidebar {
    grid-area: sidebar;
  }
  .main {
    grid-area: main;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }
</style>
