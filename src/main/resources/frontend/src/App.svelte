<script>
  import Backend from './Backend';
  import Login from "./Login.svelte";
  import Signup from "./Signup.svelte";
  import MainScreen from './MainScreen.svelte';

  let authState = "PENDING_AUTH"; // STATES: UNAUTH, PENDING_AUTH, AUTHED

  let backend = new Backend();
  backend.isAuthenticated().then(isAuthenticated => {
    if (isAuthenticated) {
      authState = "AUTHED";
    } else {
      authState = "UNAUTH";
    }
  });

  async function handleLoginSubmit(evt) {
    let {username, password} = evt.detail;
    authState = "PENDING_AUTH";
    let isAuthenticated = await backend.authenticate(username, password)
    if (isAuthenticated) {
      authState = "AUTHED";
    } else {
      authState = "UNAUTH";
    }
  }

  async function handleSignupSubmit(evt) {
    let {username, password} = evt.detail;
    authState = "PENDING_AUTH";
    let isAuthenticated = await backend.signUp(username, password)
    if (isAuthenticated) {
      authState = "AUTHED";
    } else {
      authState = "UNAUTH";
    }
  }

  function handleSignOut() {
    authState = "UNAUTH";
    backend.signOut();
  }
</script>

{#if authState == "PENDING_AUTH"}
  <p>Loading...</p>
{:else if authState == "AUTHED"}
  <MainScreen backend={backend} on:signOut={handleSignOut} />
{:else if authState == "UNAUTH"}
  <h2>Login</h2>
  <Login on:loginSubmit={handleLoginSubmit} />

  <h2>Signup</h2>
  <Signup on:signupSubmit={handleSignupSubmit} />
{/if}
