declare module 'pinia-plugin-persistedstate' {
    import { PiniaPluginContext } from 'pinia';

    export function createPersistedState(): (context: PiniaPluginContext) => void;
}