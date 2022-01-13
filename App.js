import React, { useCallback, useEffect, useState } from 'react';
import {
  Button,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  Image,
  useColorScheme,
  View,
  NativeModules,
  FlatList,
  TouchableOpacity,
} from 'react-native';

const {InstalledAppsModule} = NativeModules;

const App = () => {
  const [apps, setApps] = useState([]);
  const isDarkMode = useColorScheme() === 'dark';

  useEffect(() => {
    loadApps();
  }, [loadApps])

  const loadApps = useCallback(async () => {
    await InstalledAppsModule.getAllApps().then(val => setApps(JSON.parse(val)));
  }, []);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? '#000' : '#fff',
  };

  function compare( a, b ) {
    if ( a.label < b.label ){
      return -1;
    }
    if ( a.label > b.label ){
      return 1;
    }
    return 0;
  }

  return (
    <View style={backgroundStyle}>
      <StatusBar translucent backgroundColor="#0000" />
      <FlatList numColumns={4} contentContainerStyle={{paddingTop: 30, paddingHorizontal: 10}} data={apps.sort(compare)} renderItem={({item}) => 
      <TouchableOpacity style={{flex: 0.25, maxWidth:'25%'}} onPress={() => InstalledAppsModule.launchApplication(item.name)}>
        <View style={{alignItems: 'center', padding: 8}}>
          <Image style={{width: 50, height: 50}} width={50} height={50}  source={{uri: 'data:image/png;base64,' + item.icon}} />
          <Text style={{textAlign: 'center'}}>{item.label}</Text>
        </View>
      </TouchableOpacity>} />
    </View>
  );
};

export default App;
