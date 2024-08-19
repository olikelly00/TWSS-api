<script>
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let allTracks;
  let selected;

  function addTrack() {
    dispatch('addTrack', selected);
  }

</script>

<select bind:value={selected}>
  <option value="">Select a track</option>
  {#each allTracks as track}
    <option value={track}>{track.title} by {track.artist}</option>
  {/each}
</select>

<button type="button" on:click={addTrack}>Add</button>
