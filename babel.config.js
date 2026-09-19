module.exports = function (api) {
  api.cache(true);
  return {
    // babel-preset-expo automatically includes the react-native-worklets/plugin
    // required by Reanimated 4 when it is installed, so no extra plugin is needed.
    presets: ['babel-preset-expo'],
  };
};
