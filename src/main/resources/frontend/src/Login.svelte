<script>
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();

  let username = '';
  let password = '';

  async function submit(event) {
    dispatch('loginSubmit', { username, password });
  }

</script>

<label for="login-username">Username</label>
<input id="login-username" bind:value={username} />

<label for="login-password">Password</label>
<input type="password" id="login-password" bind:value={password} />

<button type="button" on:click={submit}>Log In</button>
